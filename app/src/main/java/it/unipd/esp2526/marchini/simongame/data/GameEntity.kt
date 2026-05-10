package it.unipd.esp2526.marchini.simongame.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity (

    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,

    @ColumnInfo(name = "sequence")
    val sequence : String?,

    @ColumnInfo(name = "error_index")
    val errorIndex : Int?,
)
