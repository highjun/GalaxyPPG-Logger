package kaist.iclab.galaxyppglogger.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        EventEntity::class,
        WearableStatEntity::class,
    ]
)
abstract class RoomDB : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun wearableStatDao(): WearableStatDao
}