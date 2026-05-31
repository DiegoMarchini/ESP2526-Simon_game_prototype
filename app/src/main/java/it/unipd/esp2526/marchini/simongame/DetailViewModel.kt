package it.unipd.esp2526.marchini.simongame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import it.unipd.esp2526.marchini.simongame.data.GameDao
import it.unipd.esp2526.marchini.simongame.data.GameEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel associato all'actvity DetailActivity
class DetailViewModel(
    application : Application,
    private val dao : GameDao
) : AndroidViewModel(application) {

    // uso una variabile privata che il ViewModel manipola, e ne espongo il valore in lettura all'UI attraverso una variabile pubblica
    // mantengo l'incapsulamento
    private val _selectedGame = MutableStateFlow(GameEntity(0, 0, "",0))
    val selectedGame : StateFlow<GameEntity> = _selectedGame.asStateFlow()

    // funzione per ottenere i dati relativi ad una partita indicandone l'ID
    fun loadGameByID(gameId : Int) {
        viewModelScope.launch(Dispatchers.IO) {_selectedGame.value = dao.getGameByID(gameId)}
    }
}

// ViewModelFactory per la creazione di oggetti DetailViewModel
// codice ispirato al codelab pubblicato su moodle
class DetailViewModelFactory(
    private val application : Application,
    private val dao : GameDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if(modelClass.isAssignableFrom(DetailViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(application, dao) as T
        }
        throw IllegalArgumentException("Classe ViewModel sconosciuta")
    }
}