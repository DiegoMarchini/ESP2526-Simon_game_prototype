package it.unipd.esp2526.marchini.simongame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.sp
import it.unipd.esp2526.marchini.simongame.data.AppDatabase
import it.unipd.esp2526.marchini.simongame.ui.theme.SimonGameTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import kotlin.getValue


class DetailActivity : ComponentActivity() {

    // creazione dell' DetailViewModel: ottengo il DAO e aggancio la variabile viewModel al risultato della DetailViewModelFactory
    private val viewModel: DetailViewModel by viewModels {
        val dao = AppDatabase.getDatabase(applicationContext).gameDao()
        DetailViewModelFactory(application, dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gameId = intent.getIntExtra("GAME_ID", -1)

        setContent {
            SimonGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScreenThree(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        viewModel = viewModel,
                        gameId = gameId
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenThree(modifier : Modifier = Modifier, viewModel: DetailViewModel, gameId : Int){

    LaunchedEffect(gameId) {  viewModel.loadGameByID(gameId) }
    val game by viewModel.selectedGame.collectAsState()

    val longestSequence = if(game.score != 0) game.sequence.take(game.score * 3 - 2) else "No sequence"
    // indice * 3 perchè la sequenza contiene anche spazi e virgole
    val correctSequence = game.sequence.take(game.errorIndex * 3)
    val errorSequence = game.sequence.substring(game.errorIndex * 3)
    val streak = (game.score * (game.score + 1) / 2) + game.errorIndex // quanti button sono stati premuti correttamente prima di sbagliare
    val sequence = buildAnnotatedString {
        append(correctSequence)
        withStyle(style = SpanStyle(color = Color.Red)){append(errorSequence)}
    }

    Column(modifier = modifier.background(Color(0xFF121824))){
        Box(
            modifier = Modifier.weight(0.3f).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ){
            Text(
                text = "${stringResource(R.string.score_detail)}: ${game.score}",
                color = Color(0xFFFBBF24),
                textAlign = Center,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier.weight(0.1f).fillMaxSize(),
            contentAlignment = Alignment.Center,

        ){
            Text(
                text = stringResource(R.string.sequence_detail),
                color = Color(0xFF94A3B8),
                fontSize = 28.sp,
                textAlign = Center,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier.weight(0.3f).fillMaxSize(),
            contentAlignment = Alignment.TopCenter,

            ){
            Text(
                text = sequence,
                fontSize = 24.sp,
                textAlign = Center,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier.weight(0.2f).fillMaxSize(),
            contentAlignment = Alignment.Center,

            ){
            Text(
                text = "${stringResource(R.string.longest_detail)}\n $longestSequence",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                textAlign = Center
            )
        }

        Box(
            modifier = Modifier.weight(0.1f).fillMaxSize(),
            contentAlignment = Alignment.TopCenter,

            ){
            Text(
                text = "${stringResource(R.string.streak_detail)}: $streak",
                fontSize = 24.sp,
                textAlign = Center,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        }
    }


}