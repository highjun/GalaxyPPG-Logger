package kaist.iclab.galaxyppglogger.collector.SkinTemp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "skin-temp",
)
data class SkinTempEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 고유 ID
    val dataReceived: Long,
    val timestamp: Long,
    val ambientTemp: Float,
    val objectTemp: Float,
    val status: Int
)
