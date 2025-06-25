package kaist.iclab.galaxyppglogger.collector.PPG

import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.PpgType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.galaxyppglogger.collector.AbstractCollector
import kaist.iclab.galaxyppglogger.data.HealthTrackerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PPGCollector(
    healthTrackerService: HealthTrackerService,
    private val ppgDao: PpgDao
) : AbstractCollector(healthTrackerService, HealthTrackerType.PPG_CONTINUOUS,
    setOf(PpgType.GREEN, PpgType.RED, PpgType.IR)) {
    override fun onData(dataPoints: MutableList<DataPoint>) {
        val dataReceived = System.currentTimeMillis()
        val ppgEntities = dataPoints.map {
            PpgEntity(
                dataReceived = dataReceived,
                timestamp = it.timestamp,
                green = it.getValue(ValueKey.PpgSet.PPG_GREEN),
                greenStatus = it.getValue(ValueKey.PpgSet.GREEN_STATUS),
                red = it.getValue(ValueKey.PpgSet.PPG_RED),
                redStatus = it.getValue(ValueKey.PpgSet.RED_STATUS),
                ir = it.getValue(ValueKey.PpgSet.PPG_IR),
                irStatus = it.getValue(ValueKey.PpgSet.IR_STATUS)
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            ppgDao.insertEvents(ppgEntities)
        }
    }
}