package kaist.iclab.galaxyppglogger.ui

import androidx.lifecycle.ViewModel
import kaist.iclab.galaxyppglogger.data.EventDao
import kaist.iclab.galaxyppglogger.data.EventEntity
import kaist.iclab.galaxyppglogger.data.WearableNodeInfo
import kaist.iclab.galaxyppglogger.data.WearableStatDao
import kaist.iclab.galaxyppglogger.data.WearableStatEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AbstractViewModel : ViewModel() {
    protected val _isMonitoringState = MutableStateFlow(false)
    val isMonitoringState: StateFlow<Boolean>
        get() = _isMonitoringState.asStateFlow()

    abstract val connectedNodeInfo: StateFlow<WearableNodeInfo?>

    abstract val wearableStatState: StateFlow<WearableStatEntity>

    protected val _lapsedTime: MutableStateFlow<Long> = MutableStateFlow(0)
    val lapsedTime: StateFlow<Long>
        get() = _lapsedTime.asStateFlow()

    abstract val eventsState: StateFlow<List<EventEntity>>

    /* Start Recording */
    abstract fun start()

    /* Stop Recording */
    abstract fun stop()

    /* Delete All Events*/
    abstract fun reset()

    /* Export data into CSV files */
    abstract fun export()

    /* Tagging event */
    abstract fun tag()
}