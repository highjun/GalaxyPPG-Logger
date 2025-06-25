package kaist.iclab.galaxyppglogger.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WearableStatDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEvents(wearableStatEntities: List<WearableStatEntity>)

    @Query("SELECT * FROM wearable ORDER BY timestamp DESC LIMIT 1")
    fun getLast(): Flow<WearableStatEntity?>

    @Query("DELETE FROM wearable")
    suspend fun deleteAll()
}