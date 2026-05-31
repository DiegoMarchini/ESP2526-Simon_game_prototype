package it.unipd.esp2526.marchini.simongame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import it.unipd.esp2526.marchini.simongame.data.GameDao
import it.unipd.esp2526.marchini.simongame.data.GameEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// ViewModel associato all'actvity HistoryActivity
class HistoryViewModel(
    application : Application,
    private val dao : GameDao
) : AndroidViewModel(application) {

    // variabile che consente la visualizzazione della lista di partite in HistoryActivity
    val allGames: StateFlow<List<GameEntity>> = dao.getAllGames()
        .stateIn(
            scope = viewModelScope, // l'aggiornamento della lista di partite è legata all'esistenza del ViewModel
            started = SharingStarted.WhileSubscribed(5000), // attendo 5 secondi dopo che la lista di partite perde il foreground prima che il ViewModel smetta di seguire il DB, ottimizzazione per la batteria
            initialValue = emptyList() // valore iniziale dello StateFlow prima che venga popolato dallle partitte prese dal DB
        )

}

// ViewModelFactory per la creazione di oggetti DetailViewModel
// codice ispirato al codelab pubblicato su moodle
class HistoryViewModelFactory(
    private val application : Application,
    private val dao : GameDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if(modelClass.isAssignableFrom(HistoryViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(application, dao) as T
        }
        throw IllegalArgumentException("Classe ViewModel sconosciuta")
    }
}