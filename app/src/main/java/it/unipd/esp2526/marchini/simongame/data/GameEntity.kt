package it.unipd.esp2526.marchini.simongame.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Int,

    @ColumnInfo(name = "sequence")
    val sequence : String?,

    @ColumnInfo(name = "error")
    val error : String?,
)
