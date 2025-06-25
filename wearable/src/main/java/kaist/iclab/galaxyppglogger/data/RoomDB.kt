package kaist.iclab.galaxyppglogger.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import kaist.iclab.galaxyppglogger.collector.ACC.AccDao
import kaist.iclab.galaxyppglogger.collector.ACC.AccEntity
import kaist.iclab.galaxyppglogger.collector.HR.HRDao
import kaist.iclab.galaxyppglogger.collector.HR.HREntity
import kaist.iclab.galaxyppglogger.collector.PPG.PpgDao
import kaist.iclab.galaxyppglogger.collector.PPG.PpgEntity
import kaist.iclab.galaxyppglogger.collector.SkinTemp.SkinTempDao
import kaist.iclab.galaxyppglogger.collector.SkinTemp.SkinTempEntity

@Database(
    version = 15,
    entities = [
        PpgEntity::class,
        AccEntity::class,
        HREntity::class,
        SkinTempEntity::class,
    ],
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class RoomDB:RoomDatabase() {
    abstract fun ppgDao(): PpgDao
    abstract fun accDao(): AccDao
    abstract fun hrDao(): HRDao
    abstract fun skinTempDao(): SkinTempDao
}

class Converters {
    @TypeConverter
    fun listToJson(value: List<Int>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String): List<Int> = Gson().fromJson(value,Array<Int>::class.java).toList()
}