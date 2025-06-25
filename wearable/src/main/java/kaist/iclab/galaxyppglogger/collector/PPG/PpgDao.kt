package kaist.iclab.galaxyppglogger.collector.PPG

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.galaxyppglogger.collector.BaseDao

@Dao
interface PpgDao:BaseDao<PpgEntity> {
    @Query("SELECT * FROM ppg")
    override suspend fun getAll(): List<PpgEntity>

    @Insert
    override suspend fun insertEvents(events: List<PpgEntity>)

    @Query("DELETE FROM ppg")
    override suspend fun deleteAll()

    @Query("SELECT * FROM ppg ORDER BY timestamp DESC LIMIT 1")
    override suspend fun getLast(): PpgEntity
}