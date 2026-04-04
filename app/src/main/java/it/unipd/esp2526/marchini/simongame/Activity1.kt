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

val buttonColors = listOf(Color.Red, Color.Green, Color.Blue,Color.Cyan,Color.Magenta, Color.Yellow)
val buttonTexts = listOf("R", "G", "B", "C", "M", "Y", "Cancella", "Fine Partita")
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

@Composable
fun ScreenOne(modifier: Modifier = Modifier, buttonAction : () -> Unit) {

    val orientation = LocalConfiguration.current.orientation

    var t by rememberSaveable { mutableStateOf("")}



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
            ColoredMatrix(
                rowModifier = rowModifier,
                buttonModifier = buttonModifier,
                buttonAction = {
                        index -> t = coloredButtonAction(index, t)
                }
            )
            }
            Column(
                modifier = modifier.padding(12.dp)
                                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){
                TextArea(
                    text = t,
                    rowModifier = rowModifier,
                    textModifier = textModifier
                )
                ButtonArea(
                    rowModifier = rowModifier,
                    buttonModifier = buttonModifier,
                    buttonAction1 = { // action del button Cancella
                        t = ""
                    },
                    buttonAction2 = buttonAction
                )
            }

        }
    }
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

            ColoredMatrix(
                rowModifier = rowModifier,
                buttonModifier = buttonModifier,
                buttonAction = {
                    index -> t = coloredButtonAction(index, t)
                }
            )
            TextArea(
                text = t,
                rowModifier = rowModifier,
                textModifier = textModifier
            )
            ButtonArea(
                rowModifier = rowModifier,
                buttonModifier = buttonModifier,
                buttonAction1 = { // action del button Cancella
                    t = ""
                },
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

    repeat(3){ // creazione delle 3 righe della matrice colorata
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ){
            repeat(2){ // creazione dei bottoni
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
        Text( // testo multiriga non editabile
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
    Row( // riga 5
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Button(
            // Cancella
            onClick = { buttonAction1() },
            modifier = buttonModifier.padding(24.dp)
        ) {
            Text(text = buttonTexts[6])
        }

        Button(
            // Fine Partita
            onClick = { buttonAction2() },
            modifier = buttonModifier.padding(24.dp)
        ) {
            Text(text = buttonTexts[7])
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ScreenOnePreview() {
    //it.unipd.esp2526.marchini.simongame.ScreenOne()
}

fun coloredButtonAction(index : Int, text : String) : String{
    val t : String
    if(text.isNotBlank())  t = text + ", " + buttonTexts[index]
    else t = buttonTexts[index]
    return t
}