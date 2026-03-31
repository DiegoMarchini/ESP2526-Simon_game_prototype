package it.unipd.esp2526.marchini.simongame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Row(modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)){

        FilledTonalButton(onClick = buttonAction) {
            Text(text = "R")
        }

        FilledTonalButton(onClick = buttonAction) {
            Text(text = "G")
        }

        FilledTonalButton(onClick = buttonAction) {
            Text(text = "B")
        }
    }

    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)){

        FilledTonalButton(onClick = buttonAction) {
            Text(text = "C")
        }

        FilledTonalButton(onClick = buttonAction) {
            Text(text = "M")
        }

        FilledTonalButton(onClick = buttonAction) {
            Text(text = "Y")
        }
    }
}

