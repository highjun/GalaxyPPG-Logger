package kaist.iclab.galaxyppglogger.collector.PPG

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "ppg",
)
data class PpgEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 고유 ID
    val dataReceived: Long,
    val timestamp: Long,
    val green: Int,
    val greenStatus: Int,
    val red: Int,
    val redStatus: Int,
    val ir: Int,
    val irStatus: Int,
)
