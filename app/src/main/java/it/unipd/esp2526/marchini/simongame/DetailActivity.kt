package it.unipd.esp2526.marchini.simongame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.sp
import it.unipd.esp2526.marchini.simongame.data.AppDatabase
import it.unipd.esp2526.marchini.simongame.data.GameDao
import it.unipd.esp2526.marchini.simongame.data.GameEntity
import it.unipd.esp2526.marchini.simongame.ui.theme.SimonGameTheme
import androidx.compose.runtime.setValue

private lateinit var dao : GameDao

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dao = AppDatabase.getDatabase(applicationContext).gameDao()
        val gameId = intent.getIntExtra("GAME_ID", -1)

        setContent {
            SimonGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScreenThree(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray)
                            .padding(innerPadding),
                        gameId = gameId
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenThree(modifier : Modifier = Modifier, gameId : Int){

    var game by remember{mutableStateOf(GameEntity(id = gameId, sequence = "", errorIndex = 0))}

    LaunchedEffect(gameId) { game = dao.getGameByID(gameId) }

    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Spacer(modifier = Modifier.weight(0.01f))

        // numero di rettangoli colorati premuti in una partita
        Text(
            modifier = Modifier.weight(0.15f),
            text = game.errorIndex.toString(),
            textAlign = Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(0.09f))

        // sequenza di rettangoli colorati premuti in una partita
        Text(
            modifier = Modifier.weight(0.7f),
            text = game.sequence,
        )
        Spacer(modifier = Modifier.weight(0.05f))
    }
}