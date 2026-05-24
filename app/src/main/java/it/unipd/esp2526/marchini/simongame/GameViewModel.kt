package it.unipd.esp2526.marchini.simongame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.unipd.esp2526.marchini.simongame.audio.SoundSynthesizer
import it.unipd.esp2526.marchini.simongame.data.GameDao
import it.unipd.esp2526.marchini.simongame.data.GameEntity
import it.unipd.esp2526.marchini.simongame.logic.GameComputer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// classe enum per rappresentare gli stati di gioco
enum class GameState {IDLE, COMPUTER_TURN, PLAYER_TURN, GAME_OVER}

// ViewModel che gestisce le chiamate eseguite da UI a elementi terzi (DB, classi di logica e utility)
// fornisce accesso ai dati all'UI esponendo variabili poi catturate dalle activity come stato di funzioni composable
// serve a evitare di dover ripetere computazioni per activity che vengono distrutte e ricreate continuamente
class GameViewModel(
    application : Application,
    private val dao : GameDao
) : AndroidViewModel(application) {

    val sound = SoundSynthesizer()
    val computer = GameComputer(
        visibleFbAction = { index -> _highlightIndex.value = index}, // callback per consentire all'oggetto GameComputer di modificare la UI
        soundFbAction = { index -> sound.playTone(index)} // callback per consentire all'oggetto GameComputer di riprodurre i suoni
    )

    // scelto l'uso di StateFlow per l'osservazione di flussi di dati da rendere visibili poi all'UI
    // uso una variabile privata che il ViewModel manipola, e ne espongo il valore in lettura  all'UI attraverso una variabile pubblica
    // mantengo l'incapsulamento

    // variabile per indicare il button considerato dal computer
    private val _highlightIndex = MutableStateFlow<Int?>(null)
    val highlightIndex : StateFlow<Int?> = _highlightIndex.asStateFlow()

    // variabile per indicare la GameEntity restituita dalla selezione tramite Id
    private val _selectedGame = MutableStateFlow<GameEntity?>(null)
    val selectedGame = _selectedGame.asStateFlow()

    // variabile per rappresentare il corrente stato di gioco
    private val _gameState = MutableStateFlow(GameState.IDLE)
    val gameState : StateFlow<GameState> = _gameState.asStateFlow()

    // variabile per tenere traccia del punteggio della partita
    private val _score = MutableStateFlow(0)
    val score : StateFlow<Int> = _score.asStateFlow()

    // variabile che consente di tenere traccia e modificare la sequenza visualizzata nell'area di testo nell'activity di gioco
    private val _userSequence = MutableStateFlow("")
    val userSequence : StateFlow<String> = _userSequence.asStateFlow()

    val buttonTexts = listOf("R", "G", "B", "C", "M", "Y") // lista di supporto

    // variabile che consente la visualizzazione della lista lista di partite in GameHistoryActivity
    val allGames: StateFlow<List<GameEntity>> = dao.getAllGames()
        .stateIn(
            scope = viewModelScope, // l'aggiornamento della lista di partite è legata all'esistenza del ViewModel
            started = SharingStarted.WhileSubscribed(5000), // attendo 5 secondi dopo che la lista di partite perde il foreground prima che il ViewModel smetta di seguire il DB, ottimizzazione per la batteria
            initialValue = emptyList() // valore iniziale dello StateFlow prima che venga popolato dallle partitte prese dal DB
        )

    fun startGame(){
        computer.resetSequence()
        _score.value = 0
        startComputerTurn()
    }

    // funzione per far iniziare il turno del computer (invocata in GameActivity)
    fun startComputerTurn(){
        viewModelScope.launch {
            _gameState.value = GameState.COMPUTER_TURN
            computer.extendSequence()
            computer.playSequence(800)
            _gameState.value = GameState.PLAYER_TURN
        }
    }

    // funzione per controllare la correttezza del button cliccato dall'utente
    fun checkMove(index : Int) {
        if(_gameState.value != GameState.PLAYER_TURN) return // ignoro la pressione di tasti se non è il turno del giocatore
        sound.playTone(index)
        viewModelScope.launch{
            _highlightIndex.value = index
            delay(500)
            _highlightIndex.value = null
        }
        val pressedButton = buttonTexts[index]
        _userSequence.value = if(_userSequence.value.isNotBlank()) " ${_userSequence.value}, $pressedButton"
                              else pressedButton
        when(computer.checkPlayerMove(index)){
            1 -> {} // bottone corretto, sequenza non finita, in attesa dell'utente
            0 -> { // bottone corretto, sequenza finita, inizia un nuovo turno
                _score.value += 1
                viewModelScope.launch {
                    delay(1000)
                    _userSequence.value = ""
                    startComputerTurn()
                }
            }
            -1 -> { // bottone sbagliato, partita terminata
                _gameState.value = GameState.GAME_OVER
                endGame()
            }
        }

    }

    fun resetComputer(){computer.resetSequence()}

    fun endGame(){
        if(_score.value == 0 && (_gameState.value == GameState.COMPUTER_TURN || _gameState.value == GameState.IDLE)) return
        val finalSequence = computer.getSequence()
        val errorIndex = computer.getErrorIndex()
        insertGame(GameEntity(score = _score.value , sequence = finalSequence, errorIndex = errorIndex))
    }

    // funzione per inserire una nuova partita (invocata alla chiusura di GameActivity)
    fun insertGame(game : GameEntity) = viewModelScope.launch(Dispatchers.IO) {
        dao.insertGame(game)
    }

    // funzione per ottenere i dati relativi ad una partita indicandone l'ID (invocata in DetailActivity)
    fun getGameByID(gameId : Int) = viewModelScope.launch{
        _selectedGame.value = dao.getGameByID(gameId)
    }

}

// ViewModelFactory per la creazione di oggetti GameViewModel
// codice ispirato al codelab pubblicato su moodle
class GameViewModelFactory(
    private val application : Application,
    private val dao : GameDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if(modelClass.isAssignableFrom(GameViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(application, dao) as T
        }
        throw IllegalArgumentException("Classe ViewModel sconosciuta")
    }
}