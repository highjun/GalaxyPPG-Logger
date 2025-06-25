package kaist.iclab.galaxyppglogger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName="wearable"
)
data class WearableStatEntity(
    @PrimaryKey
    val timestamp: Long = 0L,
    val accX: Float = 0.0f,
    val accY: Float = 0.0f,
    val accZ: Float = 0.0f,
    val ppg: Int = 0,
    val ppgStatus: Int = -1 ,
    val hr: Int = 0,
    val hrStatus: Int = 2,
)