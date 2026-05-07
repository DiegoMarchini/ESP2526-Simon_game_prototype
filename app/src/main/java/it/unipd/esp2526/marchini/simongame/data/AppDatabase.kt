package it.unipd.esp2526.marchini.simongame.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(GameEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao() : GameDao
}