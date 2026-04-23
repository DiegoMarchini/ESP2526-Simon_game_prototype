package it.unipd.esp2526.marchini.simongame

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unipd.esp2526.marchini.simongame.ui.theme.SimonGameTheme

// activity della prima schermata, contente
// matrice 3x2 colorata, area di testo e area dei bottoni "Cancella" e "Fine Partita"
class Activity1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimonGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScreenOne(
                        modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),

                        // azione che passo per generare l'intent alla pressione del button "Fine Partita"
                        buttonAction = { gamesHistory ->
                            val intent = Intent(this, Activity2::class.java)

                            // passo la lista delle partite terminate (gamesHistory) alla seconda activity via intent
                            intent.putStringArrayListExtra("GAMES_HISTORY",ArrayList(gamesHistory))
                            startActivity(intent)

                        }
                        )
                }
            }
        }
    }
}

// lista di colori e lettere associate ai button della matrice 3x2
val buttonColors = listOf(Color.Red, Color.Green, Color.Blue,Color.Cyan,Color.Magenta, Color.Yellow)
val buttonTexts = listOf("R", "G", "B", "C", "M", "Y")
@Composable
fun ScreenOne(modifier: Modifier = Modifier, buttonAction : (List<String>) -> Unit) {

    // catturo l'orientation per gestire le modalità PORTRAIT/LANDSCAPE
    val orientation = LocalConfiguration.current.orientation

    // stato di Activity1 : la sequenza contenuta nell'area di testo multiriga non editabile
    var sequence by rememberSaveable { mutableStateOf("")}

    // stato di Activity1 : la lista di sequenze giocate
    // questa lista viene passsata con un intent ad Activity2 per poi visualizzare lo storico delle partite
    var gamesHistory by rememberSaveable { mutableStateOf(listOf<String>())}

    // azione dei tasti colorati, riceve come parametro l'indice del button premuto
    // e aggiunge la lettera corrispondente al colore del tasto premuto nella sequenza
    // funzione passata come parametro al composable ColoredMatrix contenente i button colorati
    val coloredButtonAction : (Int) -> Unit = { index ->
        sequence = if(sequence.isNotBlank()){
            "$sequence, ${buttonTexts[index]}"
        } else buttonTexts[index]
    }

    // azione del tasto "Cancella", elimina la sequenza digitata
    // funzione passata come parametro al composable ButtonArea che contiene il button "Cancella"
    val deleteAction : () -> Unit = { sequence = "" }

    // azione del tasto "Fine Partita", aggiorna la lista di sequenze giocate prima di cancellare la sequenza appena terminata,
    // poi lancia un intent verso Activity2 passando come dato la lista di sequenze giocate
    // funzione passata come parametro al composable ButtonArea che contiene il button "Fine Partita"
    val endGameAction : () -> Unit = {
        gamesHistory += sequence
        sequence = ""
        buttonAction(gamesHistory)
    }

    // adottato l'uso di Compose con componenti "rigide" per il layout (annidando row e column)
    // piuttosto che l'imposizione di vincoli tra oggetti

    // LAYOUT in modalità LANDSCAPE : nella colonna di sx il composable ColoredMatrix,
    // nella colonna di dx i composable TextArea e ButtonArea
    if(orientation == Configuration.ORIENTATION_LANDSCAPE){
        Row(
            modifier = modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ){
            Column(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){

            // matrice 3x2 di button colorati
            ColoredMatrix(
                modifier = Modifier.weight(1f),
                buttonAction = { index -> coloredButtonAction(index) }
            )
            }
            Column(
                modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp)
                                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){
                // area di testo multiriga non editabile
                TextArea(
                    modifier = Modifier.weight(1f),
                    text = sequence
                )

                // area dei button "Cancella" e "Fine Partita"
                ButtonArea(
                    modifier = Modifier.weight(1f),
                    deleteAction = deleteAction, // azione del button "Cancella"
                    endGameAction = endGameAction // azione del button "Fine Partita"
                )
            }
        }
    }

    // LAYOUT in modalità PORTRAIT : i 3 composable ColoredMatrix, TextArea e ButtonArea in colonna
    else{
        Column(
            modifier = modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // matrice 3x2 di button colorati
            ColoredMatrix(
                modifier = Modifier.weight(1f),
                buttonAction = { index -> coloredButtonAction(index) }
            )

            // area di testo multiriga non editabile
            TextArea(
                modifier = Modifier.weight(1f).padding(top = 8.dp),
                text = sequence
            )

            // area dei button "Cancella" e "Fine Partita"
            ButtonArea(
                modifier = Modifier.weight(1f),
                deleteAction = deleteAction, // azione del button "Cancella"
                endGameAction = endGameAction // azione del button "Fine Partita"
            )
        }
    }
}

// matrice 3x2 di button colorati
@Composable
fun ColoredMatrix(
        modifier : Modifier,
        buttonAction : (Int) -> Unit
){
    var index = 0 // indice per utilizzare i valori diversi da bottone a bottone

    // creazione delle 3 righe della matrice colorata
    repeat(3){
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ){
            // creazione dei 2 button per ciascun riga
            repeat(2){
                val i = index // fisso il valore assunto da index in questa iterazione
                Button(
                    // azione passata come parametro a ColoredMatrix (vedere la definizione di coloredButtonAction in ScreenOne)
                    onClick = { buttonAction(i) },
                    modifier = modifier.fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(buttonColors[index]),
                    shape = RectangleShape,
                    border = BorderStroke(2.dp, Color.DarkGray)
                ) {}
                index++
            }
        }
    }
}

// area di testo multiriga non editabile
@Composable
fun TextArea(
        modifier : Modifier,
        text : String
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                modifier = modifier,
                textAlign = Center,
                color = Color.Black,
                text = text
            )
        }

    }
}

// area dei button "Cancella" e "Fine Partita"
@Composable
fun ButtonArea(
        modifier : Modifier,
        deleteAction : () -> Unit,
        endGameAction : () -> Unit
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // button "Cancella"
        Button(
            onClick =  deleteAction,
            modifier = modifier.fillMaxHeight().padding(vertical = 24.dp, horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.delete),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // button "Fine Partita"
        Button(
            onClick =  endGameAction,
            modifier = modifier.fillMaxHeight().padding(vertical = 24.dp, horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.end_game),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
