package kaist.iclab.galaxyppglogger.collector.SkinTemp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.galaxyppglogger.collector.BaseDao

@Dao
interface SkinTempDao:BaseDao<SkinTempEntity> {
    @Query("SELECT * FROM `skin-temp`")
    override suspend fun getAll(): List<SkinTempEntity>

    @Insert
    override suspend fun insertEvents(events: List<SkinTempEntity>)

    @Query("DELETE FROM `skin-temp`")
    override suspend fun deleteAll()

    @Query("SELECT * FROM `skin-temp` ORDER BY timestamp DESC LIMIT 1")
    override suspend fun getLast(): SkinTempEntity
}