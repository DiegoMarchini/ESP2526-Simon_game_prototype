package it.unipd.esp2526.marchini.simongame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import it.unipd.esp2526.marchini.simongame.audio.SoundSynthesizer
import it.unipd.esp2526.marchini.simongame.data.GameDao
import it.unipd.esp2526.marchini.simongame.data.GameEntity
import it.unipd.esp2526.marchini.simongame.logic.GameComputer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// classe enum per rappresentare gli stati di gioco
enum class GameState {IDLE, COMPUTER_TURN, PLAYER_TURN, PAUSE, GAME_OVER}

// ViewModel che gestisce le chiamate eseguite da UI a elementi terzi (DB, classi di logica e utility)
// fornisce accesso ai dati all'UI esponendo variabili poi catturate dalle activity come stati di funzioni composable
// serve a evitare di dover ripetere computazioni per activity che vengono distrutte e ricreate continuamente
class GameViewModel(
    application : Application,
    private val dao : GameDao,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // scelto l'uso di StateFlow per l'osservazione di flussi di dati da rendere visibili poi all'UI

    // variabili private da passare al GameComputer per sfruttare il savedStateHandle
    private val computerSequence : StateFlow<List<Int>> = savedStateHandle.getStateFlow("computer_sequence", emptyList())
    private val computerIndex : StateFlow<Int> = savedStateHandle.getStateFlow("computer_index", 0)
    private val playbackIndex : StateFlow<Int> = savedStateHandle.getStateFlow("playback_index", 0)

    // variabile per indicare il button considerato dal computer
    val highlightIndex : StateFlow<Int?> = savedStateHandle.getStateFlow("highlight_index", null)

    // variabile per rappresentare il corrente stato di gioco
    val gameState : StateFlow<GameState> = savedStateHandle.getStateFlow("game_state", GameState.IDLE)

    // variabile per tenere traccia del punteggio della partita
    val score : StateFlow<Int> = savedStateHandle.getStateFlow("score", 0)

    // variabile che consente di tenere traccia e modificare la sequenza visualizzata nell'area di testo nell'activity di gioco
    val userSequence : StateFlow<String> = savedStateHandle.getStateFlow("user_sequence", "")

    val buttonTexts = listOf("R", "G", "B", "C", "M", "Y") // lista di supporto

    val sound = SoundSynthesizer()
    val computer = GameComputer(
        visibleFbAction = { index -> savedStateHandle["highlight_index"] = index}, // callback per consentire all'oggetto GameComputer di modificare la UI
        soundFbAction = { index -> sound.playTone(index)}, // callback per consentire all'oggetto GameComputer di riprodurre i suoni
        getState = {gameState.value},
        getSequence = {computerSequence.value},
        setSequence = {newList -> savedStateHandle["computer_sequence"] = newList},
        getIndex = {computerIndex.value},
        setIndex = {newIndex -> savedStateHandle["computer_index"] = newIndex},
        getPbIndex = {playbackIndex.value},
        setPbIndex = {newIndex -> savedStateHandle["playback_index"] = newIndex}
    )

    fun startGame(){
        computer.resetSequence()
        savedStateHandle["score"] = 0
        startComputerTurn()
    }

    // funzione per far iniziare il turno del computer (invocata in GameActivity)
    fun startComputerTurn(){
        viewModelScope.launch {
            savedStateHandle["game_state"] = GameState.COMPUTER_TURN
            computer.extendSequence()
            computer.playSequence(800)
            if(gameState.value == GameState.COMPUTER_TURN) savedStateHandle["game_state"] = GameState.PLAYER_TURN
        }
    }

    // funzione per controllare la correttezza del button cliccato dall'utente
    fun checkMove(index : Int) {
        if(gameState.value != GameState.PLAYER_TURN) return // ignoro la pressione di tasti se non è il turno del giocatore
        sound.playTone(index)
        viewModelScope.launch{
            savedStateHandle["highlight_index"] = index
            delay(500)
            savedStateHandle["highlight_index"] = null
        }
        val pressedButton = buttonTexts[index]
        savedStateHandle["user_sequence"] = if(userSequence.value.isNotBlank()) " ${userSequence.value}, $pressedButton"
                              else pressedButton
        when(computer.checkPlayerMove(index)){
            1 -> {} // bottone corretto, sequenza non finita, in attesa dell'utente
            0 -> { // bottone corretto, sequenza finita, inizia un nuovo turno
                savedStateHandle["score"] = score.value + 1
                viewModelScope.launch {
                    delay(1000)
                    savedStateHandle["user_sequence"] = ""
                    startComputerTurn()
                }
            }
            -1 -> { // bottone sbagliato, partita terminata
                savedStateHandle["game_state"] = GameState.GAME_OVER
                endGame()
            }
        }

    }

    fun resetComputer(){computer.resetSequence()}

    fun pauseGame(){ // col controllo tra indice e score mi assicuro che nel lasso di tempo tra presentazione dell'ultimo elemento e passaggio al PLAYER_TURN, non si possa premere pausa
        if(gameState.value == GameState.COMPUTER_TURN && computer.getPlaybackIndex() < score.value) savedStateHandle["game_state"] = GameState.PAUSE
    }

    fun resumeGame(){
        if(gameState.value == GameState.PAUSE){
            viewModelScope.launch{
                savedStateHandle["game_state"] = GameState.COMPUTER_TURN
                computer.playSequence(800)
                if(gameState.value == GameState.COMPUTER_TURN) savedStateHandle["game_state"] = GameState.PLAYER_TURN
            }
        }
    }

    fun endGame(){
        if(score.value == 0 && (gameState.value == GameState.COMPUTER_TURN || gameState.value == GameState.IDLE)) return
        val finalSequence = computer.getColorSequence()
        val errorIndex = computer.getErrorIndex()
        insertGame(GameEntity(score = score.value , sequence = finalSequence, errorIndex = errorIndex))
    }

    // funzione per inserire una nuova partita (invocata alla chiusura di GameActivity)
    fun insertGame(game : GameEntity) = viewModelScope.launch(Dispatchers.IO) {
        dao.insertGame(game)
    }

}

// ViewModelFactory per la creazione di oggetti GameViewModel
// codice ispirato al codelab pubblicato su moodle
class GameViewModelFactory(
    private val application : Application,
    private val dao : GameDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>, extras : CreationExtras) : T {
        if(modelClass.isAssignableFrom(GameViewModel::class.java)){
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(application, dao, savedStateHandle) as T
        }
        throw IllegalArgumentException("Classe ViewModel sconosciuta")
    }
}