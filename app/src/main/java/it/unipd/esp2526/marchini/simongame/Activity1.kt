package it.unipd.esp2526.marchini.simongame

import android.R.attr.orientation
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
//import androidx.compose.runtime.R
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.unipd.esp2526.marchini.simongame.ui.theme.SimonGameTheme


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
                        buttonAction = {
                            val myIntent = Intent(this, Activity2::class.java)
                            startActivity(myIntent)
                        }
                        )
                }
            }
        }
    }
}

val buttonColors = listOf(Color.Red, Color.Green, Color.Blue,Color.Cyan,Color.Magenta, Color.Yellow)
val buttonTexts = listOf("R", "G", "B", "C", "M", "Y")
@Composable
fun ScreenOne(modifier: Modifier = Modifier, buttonAction : () -> Unit) {

    // catturo l'orientation
    val orientation = LocalConfiguration.current.orientation

    // stato dell'activity1 : il testo multiriga non editabile
    var t by rememberSaveable { mutableStateOf("")}

    // stato dell'activity1 : il numero di button premuti
    var count  by rememberSaveable { mutableIntStateOf(0) }

    // adottato l'uso di Compose con componenti "rigide" per il layout (annidando row, column, ecc)
    // piuttosto che l'imposizione di vincoli tra oggetti

    // layout se mi trovo in modalità landscape
    if(orientation == Configuration.ORIENTATION_LANDSCAPE){
        Row(
            modifier = modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val buttonModifier = Modifier
                .weight(1f)
                .fillMaxHeight()

            val rowModifier = Modifier
                .weight(1f)

            val textModifier = Modifier
                .weight(1f)
            Column(
                modifier = modifier.padding(12.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){

            // matrice 3x2 di button colorati
            ColoredMatrix(
                rowModifier = rowModifier,
                buttonModifier = buttonModifier,
                buttonAction = {
                        index -> t = coloredButtonAction(index, t)
                        count++
                }
            )
            }
            Column(
                modifier = modifier.padding(12.dp)
                                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){
                // area di testo multiriga non editabile
                TextArea(
                    text = t,
                    rowModifier = rowModifier,
                    textModifier = textModifier
                )

                // area dei button "Cancella" e "Fine Partita"
                ButtonArea(
                    rowModifier = rowModifier,
                    buttonModifier = buttonModifier,

                    // azione del button "Cancella"
                    buttonAction1 = {
                        t = ""
                        count = 0
                    },

                    // azione del button "Fine Partita"
                    buttonAction2 = buttonAction
                )
            }
        }
    }

    // layout se mi trovo in modalità portrait
    else{
        Column(
            modifier = modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val buttonModifier = Modifier
                .weight(1f)
                .fillMaxHeight()

            val rowModifier = Modifier
                .weight(1f)

            val textModifier = Modifier
                .weight(1f)

            // matrice 3x2 di button colorati
            ColoredMatrix(
                rowModifier = rowModifier,
                buttonModifier = buttonModifier,
                buttonAction = {
                    index -> t = coloredButtonAction(index, t)
                    count++
                }
            )

            // area di testo multiriga non editabile
            TextArea(
                text = t,
                rowModifier = rowModifier,
                textModifier = textModifier
            )

            // area dei button "Cancella" e "Fine Partita"
            ButtonArea(
                rowModifier = rowModifier,
                buttonModifier = buttonModifier,

                // azione del button "Cancella"
                buttonAction1 = {
                    t = ""
                    count = 0
                },

                // azione del button "Fine Partita"
                buttonAction2 = buttonAction
            )
        }
    }
}

@Composable
fun ColoredMatrix(
                  rowModifier : Modifier,
                  buttonModifier : Modifier,
                  buttonAction : (Int) -> Unit
){
    var index = 0 // indice per utilizzare i valori diversi da bottone a bottone

    // creazione delle 3 righe della matrice colorata
    repeat(3){
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ){
            // creazione dei bottoni
            repeat(2){
                val i = index // fisso il valore assunto da index in questa iterazione
                Button(
                    onClick = { buttonAction(i) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.buttonColors(buttonColors[index]),
                    shape = RectangleShape
                ) {
                    Text(text = buttonTexts[index])
                }
                index++
            }
        }
    }
}

@Composable
fun TextArea(
    text : String,
    rowModifier: Modifier,
    textModifier: Modifier
){
    Row( // riga 4
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // testo multiriga non editabile
        Text(
            modifier = textModifier,
            textAlign = Center,
            text = text
        )
    }
}

@Composable
fun ButtonArea(
    rowModifier: Modifier,
    buttonModifier: Modifier,
    buttonAction1: () -> Unit,
    buttonAction2: () -> Unit
){
    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // button "Cancella"
        Button(
            onClick = { buttonAction1() },
            modifier = buttonModifier.padding(24.dp)
        ) {
            Text(text = stringResource(R.string.delete))
        }

        // button "Fine Partita"
        Button(
            onClick = { buttonAction2() },
            modifier = buttonModifier.padding(24.dp)
        ) {
            Text(text = stringResource(R.string.end_game))
        }
    }
}

// azione eseguita da ogni button colorato alla pressione
fun coloredButtonAction(index : Int, text : String) : String{
    val t : String
    if(text.isNotBlank())  t = text + ", " + buttonTexts[index]
    else t = buttonTexts[index]
    return t
}