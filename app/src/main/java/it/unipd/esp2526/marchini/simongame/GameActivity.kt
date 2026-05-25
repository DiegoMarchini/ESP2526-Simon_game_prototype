package it.unipd.esp2526.marchini.simongame

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import it.unipd.esp2526.marchini.simongame.data.AppDatabase
import it.unipd.esp2526.marchini.simongame.ui.theme.SimonGameTheme
import kotlin.collections.listOf

// lista di colori e lettere associate ai button della matrice 3x2
val buttonColors = listOf(Color.Red, Color.Green, Color.Blue,Color.Cyan,Color.Magenta, Color.Yellow)

// activity della prima schermata, contente
// matrice 3x2 colorata, area di testo e area dei bottoni "Cancella" e "Fine Partita"
class GameActivity : ComponentActivity() {

    // creazione del GameViewModel: ottengo il DAO e aggancio la variabile viewModel al risultato della GameViewModelFactory
    private val viewModel: GameViewModel by viewModels {
        val dao = AppDatabase.getDatabase(applicationContext).gameDao()
        GameViewModelFactory(application, dao)
    }

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
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenOne(modifier: Modifier = Modifier, viewModel : GameViewModel) {

    val orientation = LocalConfiguration.current.orientation // catturo l'orientation per gestire le modalità PORTRAIT/LANDSCAPE
    val activity = LocalActivity.current // ottengo il contesto dell'Activity in cui è contenuto il composable per poter chiamare finish()
    val sequence by viewModel.userSequence.collectAsState() // stato di GameActivity : la sequenza contenuta nell'area di testo multiriga non editabile
    val highlightedButtonIndex by viewModel.highlightIndex.collectAsState() // indico il button messo in evidenza dal computer
    val score by viewModel.score.collectAsState()
    val gameState by viewModel.gameState.collectAsState()


    // azione dei tasti colorati, riceve come parametro l'indice del button premuto
    // e aggiunge la lettera corrispondente al colore del tasto premuto nella sequenza
    // funzione passata come parametro al composable ColoredMatrix contenente i button colorati
    val coloredButtonAction : (Int) -> Unit = { index -> viewModel.checkMove(index) }

    // azione del tasto "Avvia Partita", non fa niente
    // funzione passata come parametro al composable ButtonArea che contiene il button "Avvia Partita"
    val startGameAction : () -> Unit = { viewModel.startGame() }

    val pauseGameAction : () -> Unit = {viewModel.pauseGame()}

    val resumeGameAction : () -> Unit = {viewModel.resumeGame()}

    // azione del tasto "Fine Partita", aggiorna la lista di sequenze giocate prima di cancellare la sequenza appena terminata
    // funzione passata come parametro al composable ButtonArea che contiene il button "Fine Partita"
    val endGameAction : () -> Unit = {
        if(gameState != GameState.GAME_OVER) viewModel.endGame() // in caso di GAME OVER il gioco è già stato salvato da checkMove
        activity?.finish() }

    BackHandler(enabled = true) {endGameAction()}

    // adottato l'uso di Compose con componenti "rigide" per il layout (annidando row e column)
    // piuttosto che l'imposizione di vincoli tra oggetti

    if(gameState == GameState.GAME_OVER){
        Popup(alignment = Alignment.Center){
            Card(
                modifier = Modifier.wrapContentSize(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4A1525), contentColor = Color(0xFFFFE3E8)),
                elevation = CardDefaults.cardElevation(8.dp),
                //border = BorderStroke(4.dp, Color.Black)
            ){
                Text(text = "${stringResource(R.string.game_over)} $score", fontSize = 32.sp, lineHeight = 36.sp, textAlign = Center)
            }
        }
    }

    // LAYOUT in modalità LANDSCAPE : nella colonna di sx il composable ColoredMatrix, nella colonna di dx i composable TextArea e ButtonArea
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
                highlightedButton = highlightedButtonIndex,
                gameState = gameState,
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
                    gameState = gameState,
                    startGameAction = startGameAction, // azione del button "Cancella"
                    pauseGameAction = pauseGameAction, // azione del tasto "Pausa"
                    resumeGameAction = resumeGameAction, // azione del tasto "Riprendi"
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
                highlightedButton = highlightedButtonIndex,
                gameState = gameState,
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
                gameState = gameState,
                startGameAction = startGameAction, // azione del button "Avvia Partita"
                pauseGameAction = pauseGameAction, // azione del tasto "Pausa"
                resumeGameAction = resumeGameAction, // azione del tasto "Riprendi"
                endGameAction = endGameAction // azione del button "Fine Partita"
            )
        }
    }
}

// matrice 3x2 di button colorati
@Composable
fun ColoredMatrix(
        modifier : Modifier,
        highlightedButton : Int?,
        gameState: GameState,
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
                    enabled = gameState == GameState.PLAYER_TURN,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(highlightedButton == index) buttonColors[index] else buttonColors[index].copy(alpha = 0.6f),
                        disabledContainerColor = if(highlightedButton == index) buttonColors[index] else buttonColors[index].copy(alpha = 0.6f)
                    ),
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
        gameState: GameState,
        startGameAction : () -> Unit,
        pauseGameAction : () -> Unit,
        resumeGameAction : () -> Unit,
        endGameAction : () -> Unit
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // button "Avvia Partita"
        Button(
            onClick = startGameAction,
            enabled = gameState == GameState.IDLE,
            modifier = modifier.fillMaxHeight().padding(vertical = 24.dp, horizontal = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.start_game),
                fontSize = 16.sp,
                textAlign = Center,
                fontWeight = FontWeight.Bold
            )
        }

        // button "Pausa"
        Button(
            onClick = if(gameState == GameState.PAUSE) resumeGameAction
                      else pauseGameAction,
            enabled = (gameState == GameState.COMPUTER_TURN) || (gameState == GameState.PAUSE),
            modifier = modifier.fillMaxHeight().padding(vertical = 24.dp)
        ) {
            Text(
                text = when(gameState){
                    GameState.COMPUTER_TURN -> stringResource(R.string.pause_game)
                    GameState.PAUSE -> stringResource(R.string.resume_game)
                    else -> "-"
                },
                fontSize = 16.sp,
                textAlign = Center,
                fontWeight = FontWeight.Bold,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }

        // button "Fine Partita"
        Button(
            onClick = endGameAction,
            enabled = gameState != GameState.IDLE,
            modifier = modifier.fillMaxHeight().padding(vertical = 24.dp, horizontal = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.end_game),
                fontSize = 16.sp,
                textAlign = Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
