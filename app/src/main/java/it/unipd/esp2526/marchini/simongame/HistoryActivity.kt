package it.unipd.esp2526.marchini.simongame

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unipd.esp2526.marchini.simongame.data.AppDatabase
import it.unipd.esp2526.marchini.simongame.data.GameEntity
import it.unipd.esp2526.marchini.simongame.ui.theme.SimonGameTheme
import kotlin.getValue

// activity contente la lista di partite giocate
class HistoryActivity : ComponentActivity() {

    // creazione dell' HistoryViewModel: ottengo il DAO e aggancio la variabile viewModel al risultato della HistoryViewModelFactory
    private val viewModel: HistoryViewModel by viewModels {
        val dao = AppDatabase.getDatabase(applicationContext).gameDao()
        HistoryViewModelFactory(application, dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimonGameTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = { // button che consente di cominciare una nuova partita
                        ExtendedFloatingActionButton(
                        onClick = {
                            val intent = Intent(this, GameActivity::class.java)
                            startActivity(intent)
                        },
                        icon = { Icon(Icons.Filled.Games, getString(R.string.new_game))},
                        text = { Text(getString(R.string.new_game))})
                    }
                ) { innerPadding ->
                    ScreenTwo(modifier = Modifier.fillMaxSize().padding(innerPadding), viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ScreenTwo(modifier: Modifier = Modifier, viewModel : HistoryViewModel){

    val games by viewModel.allGames.collectAsState(initial = emptyList())

    // catturo l'orientation per gestire le modalità PORTRAIT/LANDSCAPE
    val orientation = LocalConfiguration.current.orientation

    // modifico la porzione di schermo occupata dal titolo e la dimensione del padding laterale
    // della lista a seconda della modalità
    val titleBox : Float
    val lateralPadding : Dp
    if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
        titleBox = 0.3f
        lateralPadding = 16.dp
    }
    else {
        titleBox = 0.1f
        lateralPadding = 12.dp
    }

    Column(
        modifier = modifier.padding(vertical = 12.dp, horizontal = lateralPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ){
        // box contenente il titolo "Storico Partite"
        Box(
            modifier = Modifier
                .fillMaxHeight(titleBox)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ){
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = Center,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                text = stringResource(R.string.games_history)
            )
        }
        // lista dinamica popolata dalle sequenze giocate
        GamesList(games)
    }
}

@Composable
fun GamesList(games : List<GameEntity>){


    // lista dinamica delle partite (sequenze digitate), in alto si trovano le sequenze delle partite più recenti
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp, 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        // gli oggetti della lista di sequenze
        items(games.reversed()){
            gameEntity -> GameStatsRow(gameEntity)
        }
    }
}

// riga nella LazyColumn rappresentante una partita
@Composable
fun GameStatsRow(game : GameEntity){

    val context = LocalContext.current

    // indice * 3 perchè la sequenza contiene anche spazi e virgole
    val correctSequence = game.sequence.take(game.errorIndex * 3)
    val errorSequence = game.sequence.substring(game.errorIndex * 3)

    // sequenza finale mostrata con due colori diversi
    val sequence = buildAnnotatedString {
        append(correctSequence)
        withStyle(style = SpanStyle(color = Color.Red)){append(errorSequence)}
    }

    // riga dedicata ad una partita, contiene numero di rettangoli colorati premuti e sequenza (opportunamente spaziati)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Gray, RoundedCornerShape(20))
            .clickable{
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("GAME_ID", game.id)
                context.startActivity(intent)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Spacer(modifier = Modifier.weight(0.01f))

        // numero di rettangoli colorati premuti in una partita
        Text(
            modifier = Modifier.weight(0.15f),
            text = game.score.toString(),
            textAlign = Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(0.09f))

        // sequenza di rettangoli colorati premuti in una partita
        Text(
            modifier = Modifier.weight(0.7f),
            text = sequence,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.weight(0.05f))
    }
}