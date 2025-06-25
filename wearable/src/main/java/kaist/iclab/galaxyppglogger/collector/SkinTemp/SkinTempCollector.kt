package kaist.iclab.galaxyppglogger.collector.SkinTemp

import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.galaxyppglogger.collector.AbstractCollector
import kaist.iclab.galaxyppglogger.data.HealthTrackerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SkinTempCollector(
    healthTrackerService: HealthTrackerService,
    private val skinTempDao: SkinTempDao,
) : AbstractCollector(healthTrackerService, HealthTrackerType.SKIN_TEMPERATURE_CONTINUOUS) {
    override fun onData(dataPoints: MutableList<DataPoint>) {
        val dataReceived = System.currentTimeMillis()
        val skinTempEntities = dataPoints.map {
            SkinTempEntity(
                dataReceived = dataReceived,
                timestamp = it.timestamp,
                ambientTemp = it.getValue(ValueKey.SkinTemperatureSet.AMBIENT_TEMPERATURE),
                objectTemp = it.getValue(ValueKey.SkinTemperatureSet.OBJECT_TEMPERATURE),
                status = it.getValue(ValueKey.SkinTemperatureSet.STATUS)
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            skinTempDao.insertEvents(skinTempEntities)
        }
    }
}