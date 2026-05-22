package it.unipd.esp2526.marchini.simongame.logic

import kotlinx.coroutines.delay
import kotlin.random.Random

// classe che contiene la logica dietro la generazione e la presentazione di sequenze di button da premere
class GameComputer(
    private val visibleFbAction: (Int?) -> Unit,
    private val soundFbAction: (Int?) -> Unit
) {
    private val buttons = listOf("R", "G", "B", "C", "M", "Y")
    private val sequence = mutableListOf<Int>()
    private var currentIndex = 0

    // funzione che estende la sequenza di un button alla volta
    fun extendSequence() {
        val nextButton = Random.nextInt(buttons.size)
        sequence.add(nextButton)
    }

    fun getSequence() : String { return sequence.joinToString(", "){index -> buttons[index]} }

    fun getErrorIndex() : Int { return currentIndex }
    fun resetSequence() {
        sequence.clear()
    }

    // funzione che riproduce all'utente la sequenza con feedback visivi e uditivi
    // le funzioni usate per il feedback sono passate tramite parametro alla classe GameComputer
    suspend fun playSequence(pauseMillis : Long){
        for(button in sequence){
            visibleFbAction(button)
            soundFbAction(button)
            delay(pauseMillis)
            // fermo i feedback
            visibleFbAction(null)
            //soundFbAction(null)
            delay(pauseMillis/4) // pausa breve per impedire un feedback continuo in caso ci siano due colori uguali consecutivi nella sequenza
        }
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