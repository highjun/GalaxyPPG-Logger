package kaist.iclab.galaxyppglogger.collector

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.PpgType
import kaist.iclab.galaxyppglogger.data.HealthTrackerService

abstract class AbstractCollector(
    healthTrackerService: HealthTrackerService,
    healthTrackerType: HealthTrackerType,
    setPpgTypes: Set<PpgType>? = null
) {
    private val TAG = javaClass.simpleName
    private val healthTracker: HealthTracker = if(setPpgTypes!= null)
        healthTrackerService.healthTrackingService.getHealthTracker(healthTrackerType, setPpgTypes)
        else healthTrackerService.healthTrackingService.getHealthTracker(healthTrackerType)

    private val healthTrackerEventListener: TrackerEventListener =
        object : TrackerEventListener {
            override fun onError(trackerError: HealthTracker.TrackerError) {
                Log.d(TAG, "onError")
                when (trackerError) {
                    HealthTracker.TrackerError.PERMISSION_ERROR -> Log.e(
                        TAG,
                        "ERROR: Permission Failed"
                    )

                    HealthTracker.TrackerError.SDK_POLICY_ERROR -> Log.e(
                        TAG,
                        "ERROR: SDK Policy Error"
                    )

                    else -> Log.e(TAG, "ERROR: Unknown ${trackerError.name}")
                }
            }

            override fun onDataReceived(p0: MutableList<DataPoint>) {
                onData(p0)
            }

            override fun onFlushCompleted() {
                Log.d(TAG, "onFlushCompleted")
            }
        }

    abstract fun onData(dataPoints: MutableList<DataPoint>)

    fun start() {
        try{
            healthTracker.setEventListener(healthTrackerEventListener)
        } catch(e: Exception){
            Log.e(TAG, "START: $e")
        }
    }

    fun stop() {
        healthTracker.unsetEventListener()
    }
}