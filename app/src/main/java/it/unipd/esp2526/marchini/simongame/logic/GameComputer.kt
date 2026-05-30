package it.unipd.esp2526.marchini.simongame.logic

import androidx.collection.MutableIntList
import it.unipd.esp2526.marchini.simongame.GameState
import kotlinx.coroutines.delay
import kotlin.random.Random

// classe che contiene la logica dietro la generazione e la presentazione di sequenze di button da premere
class GameComputer(
    private val visibleFbAction: (Int?) -> Unit,
    private val soundFbAction: (Int?) -> Unit,
    private val getState : () -> GameState,
    private val getSequence : () -> List<Int>,
    private val setSequence : (List<Int>) -> Unit,
    private val getIndex : () -> Int,
    private val setIndex : (Int) -> Unit,
    private val getPbIndex : () -> Int,
    private val setPbIndex : (Int) -> Unit
) {
    private val buttons = listOf("R", "G", "B", "C", "M", "Y")

    // funzione che estende la sequenza di un button alla volta
    fun extendSequence() {
        val nextButton = Random.nextInt(buttons.size)
        val sequence = getSequence().toMutableList()
        sequence.add(nextButton)
        setSequence(sequence)
    }

    fun getColorSequence() : String { return getSequence().joinToString(", "){index -> buttons[index]} }
    fun getErrorIndex() : Int { return getIndex() }
    fun getPlaybackIndex() : Int { return getPbIndex()}
    fun resetSequence() {
        setSequence(emptyList())
    }

    // funzione che riproduce all'utente la sequenza con feedback visivi e uditivi
    // le funzioni usate per il feedback sono passate tramite parametro alla classe GameComputer
    suspend fun playSequence(pauseMillis : Long){
        val sequence = getSequence()
        while(getPbIndex() < sequence.size){
            if(getState() == GameState.PAUSE){
                visibleFbAction(null)
                //soundFbAction(null)
                break
            }
            val button = sequence[getPbIndex()]
            visibleFbAction(button)
            soundFbAction(button)
            delay(pauseMillis)
            // fermo i feedback
            visibleFbAction(null)
            //soundFbAction(null)
            delay(pauseMillis/4) // pausa breve per impedire un feedback continuo in caso ci siano due colori uguali consecutivi nella sequenza
            setPbIndex(getPbIndex() + 1)
        }
        if(getPbIndex() >= sequence.size) setPbIndex(0) // finita la presentazione di una sequenza mi preparo al turno successivo
    }

    fun checkPlayerMove(playerIndex : Int) : Int {
        var feedback = 1
        val sequence = getSequence()
        if(playerIndex == sequence[getIndex()]) {
            setIndex(getIndex() + 1)
            if (getIndex() >= sequence.size){
                setIndex(0)
                feedback = 0
            }
        }
        else feedback = -1
        return feedback
    }
}