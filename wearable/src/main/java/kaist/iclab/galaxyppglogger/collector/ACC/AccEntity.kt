package kaist.iclab.galaxyppglogger.collector.ACC

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "acc",
)
data class AccEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 고유 ID
    val dataReceived: Long,
    val timestamp: Long,
    val x : Int,
    val y : Int,
    val z : Int,
)
