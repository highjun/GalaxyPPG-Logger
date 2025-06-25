package kaist.iclab.galaxyppglogger.collector.HR

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.galaxyppglogger.collector.BaseDao

@Dao
interface HRDao:BaseDao<HREntity> {
    @Query("SELECT * FROM hr")
    override suspend fun getAll(): List<HREntity>

    @Insert
    override suspend fun insertEvents(events: List<HREntity>)

    @Query("DELETE FROM hr")
    override suspend fun deleteAll()

    @Query("SELECT * FROM hr ORDER BY timestamp DESC LIMIT 1")
    override suspend fun getLast(): HREntity
}