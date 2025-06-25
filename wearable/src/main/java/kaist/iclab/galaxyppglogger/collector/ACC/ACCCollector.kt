package kaist.iclab.galaxyppglogger.collector.ACC

import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.galaxyppglogger.collector.AbstractCollector
import kaist.iclab.galaxyppglogger.data.HealthTrackerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ACCCollector(
    healthTrackerService: HealthTrackerService,
    private val accDao: AccDao,
) : AbstractCollector(healthTrackerService, HealthTrackerType.ACCELEROMETER_CONTINUOUS) {
    override fun onData(dataPoints: MutableList<DataPoint>) {
        val dataReceived = System.currentTimeMillis()
        val accEntities = dataPoints.map {
            AccEntity(
                dataReceived = dataReceived,
                timestamp = it.timestamp,
                x = it.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_X),
                y = it.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Y),
                z = it.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Z),
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            accDao.insertEvents(accEntities)
        }
    }
}