package it.unipd.esp2526.marchini.simongame.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao{

    @Insert
    suspend fun insertGame(gameEntity : GameEntity)

    @Query("SELECT * FROM games")
    fun getAllGames() : Flow<List<GameEntity>> // scelto l'uso di Flow anzichè LiveData perchè più moderno e meglio integrato con Compose

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameByID(gameId : Int) : GameEntity

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    @Delete
    suspend fun deleteGame(gameEntity : GameEntity)

}
