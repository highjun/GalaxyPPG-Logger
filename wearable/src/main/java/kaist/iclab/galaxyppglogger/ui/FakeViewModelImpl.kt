package kaist.iclab.galaxyppglogger.ui

import android.util.Log
import kaist.iclab.galaxyppglogger.collector.CollectorController
import kaist.iclab.galaxyppglogger.data.NodeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeViewModelImpl(
    private val collectorController: CollectorController,
) : AbstractViewModel() {
    private val TAG = javaClass.simpleName

    private val _serviceState = MutableStateFlow(ServiceState.DISCONNECTED)
    override val serviceState
        get() = _serviceState.asStateFlow()

    private val _localNodeInfo = MutableStateFlow<NodeInfo?>(null)
    override val localNodeInfo
        get() = _localNodeInfo.asStateFlow()

    /* start collecting data */
    override fun start() {
        Log.d(TAG, "start")
        _serviceState.value = ServiceState.RUNNING
    }

    /* stop collecting data */
    override fun stop() {
        Log.d(TAG, "stop")
        _serviceState.value = ServiceState.READY
    }

    override fun export() {
        Log.d(TAG, "export")
    }

    /* delete all data */
    override fun flush() {
        Log.d(TAG, "flush")
    }

    override fun bindService() {
        Log.d(TAG, "bindService")
    }

    override fun unbindService() {
        Log.d(TAG, "unbindService")
    }
}