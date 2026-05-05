package it.unipd.esp2526.marchini.simongame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unipd.esp2526.marchini.simongame.ui.theme.SimonGameTheme

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sequenceLength = intent.getCharSequenceExtra("LENGTH") ?: String
        val sequence = intent.getCharSequenceExtra("SEQUENCE") ?: String

        setContent {
            SimonGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScreenThree(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray)
                            .padding(innerPadding),
                        sequenceLength = sequenceLength.toString(),
                        sequence = sequence.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenThree(modifier : Modifier = Modifier, sequenceLength : String, sequence : String){
    Row(
        modifier = modifier
            .fillMaxSize(),
            //.wrapContentHeight()
            //.background(Color.DarkGray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Spacer(modifier = Modifier.weight(0.01f))

        // numero di rettangoli colorati premuti in una partita
        Text(
            modifier = Modifier.weight(0.15f),
            text = sequenceLength,
            textAlign = Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(0.09f))

        // sequenza di rettangoli colorati premuti in una partita
        Text(
            modifier = Modifier.weight(0.7f),
            text = sequence,
        )
        Spacer(modifier = Modifier.weight(0.05f))
    }
}