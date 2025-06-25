package kaist.iclab.galaxyppglogger.ui

import android.util.Log
import kaist.iclab.galaxyppglogger.data.EventDao
import kaist.iclab.galaxyppglogger.data.EventEntity
import kaist.iclab.galaxyppglogger.data.WearableNodeInfo
import kaist.iclab.galaxyppglogger.data.WearableStatDao
import kaist.iclab.galaxyppglogger.data.WearableStatEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeViewModelImpl(
    wearableStatDao: WearableStatDao,
    eventDao: EventDao
): AbstractViewModel() {
    override val wearableStatState: StateFlow<WearableStatEntity>
        = MutableStateFlow(WearableStatEntity())
    override val connectedNodeInfo: StateFlow<WearableNodeInfo?>
        = MutableStateFlow(null)
    override val eventsState: StateFlow<List<EventEntity>>
        = MutableStateFlow(listOf())

    companion object{
        const val TAG = "FakeMainViewModel"
    }
    override fun tag() {
        Log.d(TAG, "tag")
    }

    override fun export() {
        Log.d(TAG, "export")
    }

    override fun reset() {
        Log.d(TAG, "reset")
    }

    override fun stop() {
        _isMonitoringState.value = false
    }

    override fun start() {
        _isMonitoringState.value = true
    }
}