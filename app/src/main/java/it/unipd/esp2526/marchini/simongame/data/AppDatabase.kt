package it.unipd.esp2526.marchini.simongame.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GameEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao() : GameDao

    // utilizzo il design pattern singleton per assicurarmi che il db non venga duplicato e che
    // tutte le componenti dell'app interagiscano con la stessa istanza del db
    companion object {

        // @Volatile garantisce che l'istanza del db (INSTANCE) sia aggiornata e visibile per tutti i thread che ne fanno uso
        @Volatile
        private var INSTANCE : AppDatabase? = null

        // restituisce INSTANCE se non è null, altrimenti crea una nuova istanza del db e la restituisce
        fun getDatabase(context : Context) : AppDatabase {

            // synchronized evita che più components creino più istanze del db contemporaneamente
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "simon_game_db").build()
                INSTANCE = instance
                instance
            }


        }
    }
}