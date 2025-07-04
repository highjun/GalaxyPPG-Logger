package kaist.iclab.galaxyppglogger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName="event"
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
)
