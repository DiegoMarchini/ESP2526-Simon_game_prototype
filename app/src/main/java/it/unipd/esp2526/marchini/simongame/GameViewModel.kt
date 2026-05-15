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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    // variabile che consente la visualizzazione della lista lista di partite in GameHistoryActivity
    val allGames: StateFlow<List<GameEntity>> = dao.getAllGames()
        .stateIn(
            scope = viewModelScope, // l'aggiornamento della lista di partite è legata all'esistenza del ViewModel
            started = SharingStarted.WhileSubscribed(5000), // attendo 5 secondi dopo che la lista di partite perde il foreground prima che il ViewModel smetta di seguire il DB, ottimizzazione per la batteria
            initialValue = emptyList() // valore iniziale dello StateFlow prima che venga popolato dallle partitte prese dal DB
        )

    // funzione per far iniziare il turno del computer (invocata in GameActivity)
    fun startComputerTurn(){
        viewModelScope.launch {
            computer.extendSequence()
            computer.playSequence(800)
        }
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