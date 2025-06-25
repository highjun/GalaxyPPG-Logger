package kaist.iclab.galaxyppglogger.collector.ACC

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.galaxyppglogger.collector.BaseDao

@Dao
interface AccDao: BaseDao<AccEntity> {
    @Query("SELECT * FROM acc")
    override suspend fun getAll(): List<AccEntity>

    @Insert
    override suspend fun insertEvents(events: List<AccEntity>)

    @Query("DELETE FROM acc")
    override suspend fun deleteAll()

    @Query("SELECT * FROM acc ORDER BY timestamp DESC LIMIT 1")
    override suspend fun getLast(): AccEntity
}