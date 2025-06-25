package kaist.iclab.galaxyppglogger.ui

import android.app.Service
import androidx.lifecycle.ViewModel
import kaist.iclab.galaxyppglogger.data.NodeInfo
import kotlinx.coroutines.flow.StateFlow

abstract class AbstractViewModel: ViewModel() {
    abstract val serviceState : StateFlow<ServiceState>
    abstract val localNodeInfo: StateFlow<NodeInfo?>

    /* start collecting data */
    abstract fun start()

    /* stop collecting data */
    abstract fun stop()

    /* export data*/
    abstract fun export()

    /* delete all data */
    abstract fun flush()

    abstract fun bindService()
    abstract fun unbindService()
}