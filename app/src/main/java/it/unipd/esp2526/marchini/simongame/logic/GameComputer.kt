package it.unipd.esp2526.marchini.simongame.logic

import androidx.lifecycle.SavedStateHandle
import it.unipd.esp2526.marchini.simongame.GameState
import kotlinx.coroutines.delay
import kotlin.random.Random

// classe che contiene la logica dietro la generazione e la presentazione di sequenze di button da premere
class GameComputer(
    private val visibleFbAction: (Int?) -> Unit,
    private val soundFbAction: (Int?) -> Unit,
    private val getState : () -> GameState,
    private val handle : SavedStateHandle
) {
    private val buttons = listOf("R", "G", "B", "C", "M", "Y")

    private var sequence : List<Int>
        get() = handle["computer_sequence"] ?: emptyList()
        set(value) {handle["computer_sequence"] = value}

    private var currentIndex : Int
        get() = handle["computer_index"] ?: 0
        set(value) {handle["computer_index"] = value}

    private var playbackIndex : Int
        get() = handle["playback_index"] ?: 0
        set(value) {handle["playback_index"] = value}


    // funzione che estende la sequenza di un button alla volta
    fun extendSequence() {
        val nextButton = Random.nextInt(buttons.size)
        sequence = sequence.toMutableList().apply{add(nextButton)}
    }

    fun getColorSequence() : String { return sequence.joinToString(", "){index -> buttons[index]} }

    fun resetSequence() {
        sequence = emptyList()
        currentIndex = 0
        playbackIndex = 0
    }

    // funzione che riproduce all'utente la sequenza con feedback visivi e uditivi
    // le funzioni usate per il feedback sono passate tramite parametro alla classe GameComputer
    suspend fun playSequence(pauseMillis : Long){
        while(playbackIndex < sequence.size){
            if(getState() == GameState.PAUSE){
                visibleFbAction(null)
                //soundFbAction(null)
                break
            }
            val button = sequence[playbackIndex]
            visibleFbAction(button)
            soundFbAction(button)
            delay(pauseMillis)
            // fermo i feedback
            visibleFbAction(null)
            //soundFbAction(null)
            delay(pauseMillis/4) // pausa breve per impedire un feedback continuo in caso ci siano due colori uguali consecutivi nella sequenza
            playbackIndex++
        }
        if(playbackIndex >= sequence.size) playbackIndex = 0 // finita la presentazione di una sequenza mi preparo al turno successivo
    }

    fun checkPlayerMove(playerIndex : Int) : Int {
        var feedback = 1

        if(playerIndex == sequence[currentIndex]) {
            currentIndex++
            if (currentIndex >= sequence.size){
                currentIndex = 0
                feedback = 0
            }
        }
        else feedback = -1
        return feedback
    }
}