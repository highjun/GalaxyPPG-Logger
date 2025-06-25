package kaist.iclab.galaxyppglogger.data

import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HealthTrackerService(
    androidContext: Context
) {
    private val TAG = javaClass.simpleName
    private val _connectionStateFlow = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionStateFlow
        get() = _connectionStateFlow.asStateFlow()

    private val connectionListener: ConnectionListener = object: ConnectionListener {
        override fun onConnectionSuccess() {
            Log.d(TAG, "Connection Success")
            _connectionStateFlow.value = ConnectionState.CONNECTED
        }

        override fun onConnectionEnded() {
            Log.d(TAG, "Connection Ended")
            _connectionStateFlow.value = ConnectionState.DISCONNECTED
        }

        override fun onConnectionFailed(e: HealthTrackerException?) {
            Log.e(TAG, "Connection Failed: $e")
            _connectionStateFlow.value = ConnectionState.DISCONNECTED
        }
    }
    val healthTrackingService: HealthTrackingService = HealthTrackingService(connectionListener, androidContext)
    fun start(){
        healthTrackingService.connectService()
    }

    fun finish(){
        healthTrackingService.disconnectService()
    }
}