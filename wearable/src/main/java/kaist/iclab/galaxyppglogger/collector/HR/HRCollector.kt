package kaist.iclab.galaxyppglogger.collector.HR

import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.galaxyppglogger.collector.AbstractCollector
import kaist.iclab.galaxyppglogger.data.HealthTrackerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HRCollector(
    healthTrackerService: HealthTrackerService,
    private val hrDao: HRDao,
) : AbstractCollector(healthTrackerService, HealthTrackerType.HEART_RATE_CONTINUOUS) {
    override fun onData(dataPoints: MutableList<DataPoint>) {
        val dataReceived = System.currentTimeMillis()
        val hrEntities = dataPoints.map {
                HREntity(
                    dataReceived = dataReceived,
                    timestamp = it.timestamp,
                    hr = it.getValue(ValueKey.HeartRateSet.HEART_RATE),
                    hrStatus = it.getValue(ValueKey.HeartRateSet.HEART_RATE_STATUS),
                    ibi = it.getValue(ValueKey.HeartRateSet.IBI_LIST),
                    ibiStatus = it.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST),
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                hrDao.insertEvents(hrEntities)
            }
    }
}