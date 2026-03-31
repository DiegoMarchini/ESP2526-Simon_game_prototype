package it.unipd.esp2526.marchini.simongame

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
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

                        buttonAction = {}

                        )
                }
            }
        }
    }
}

@Composable
fun ScreenOne(modifier: Modifier = Modifier, buttonAction: () -> Unit) {

    val buttonColors = listOf(Color.Red, Color.Green, Color.Blue,Color.Cyan,Color.Magenta, Color.Yellow)
    val buttonTexts = listOf("R", "G", "B", "C", "M", "Y")

    Column(modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally){
        val buttonModifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        val rowModifier = Modifier
            .weight(1f)
        Row(
            // modifier = modifier,
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Button(onClick = buttonAction, modifier = buttonModifier, colors = ButtonDefaults.buttonColors(buttonColors[0]), shape = RectangleShape) {
                Text(text = buttonTexts[0])
            }

            Button(onClick = buttonAction, modifier = buttonModifier,colors = ButtonDefaults.buttonColors(buttonColors[1]), shape = RectangleShape) {
                Text(text = buttonTexts[1])
            }

        }

        Row(
            // modifier = modifier,
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Button(onClick = buttonAction, modifier = buttonModifier, colors = ButtonDefaults.buttonColors(buttonColors[2]), shape = RectangleShape) {
                Text(text = buttonTexts[2])
            }

            Button(onClick = buttonAction, modifier = buttonModifier, colors = ButtonDefaults.buttonColors(buttonColors[3]), shape = RectangleShape) {
                Text(text = buttonTexts[3])
            }

        }
        Row(
            // modifier = modifier,
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Button(onClick = buttonAction, modifier = buttonModifier, colors = ButtonDefaults.buttonColors(buttonColors[4]), shape = RectangleShape) {
                Text(text = buttonTexts[4])
            }

            Button(onClick = buttonAction, modifier = buttonModifier, colors = ButtonDefaults.buttonColors(buttonColors[5]), shape = RectangleShape) {
                Text(text = buttonTexts[5])
            }

        }
    }
}
@Preview(showBackground = true)
@Composable
fun ScreenOnePreview() {
    it.unipd.esp2526.marchini.simongame.ScreenOne(buttonAction = {})
}
