package it.unipd.esp2526.marchini.simongame.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity rappresentante una partita giocata, contiene:
// id della partita, punteggio, sequenza finale, indice della sequenza dove è avvenuto l'errore
@Entity(tableName = "games")
data class GameEntity (

    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,

    @ColumnInfo(name = "score")
    val score : Int,

    @ColumnInfo(name = "sequence")
    val sequence : String,

    @ColumnInfo(name = "error_index")
    val errorIndex : Int,
)
