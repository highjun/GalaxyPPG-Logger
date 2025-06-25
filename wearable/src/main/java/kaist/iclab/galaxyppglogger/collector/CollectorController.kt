package kaist.iclab.galaxyppglogger.collector

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class CollectorController(
    val collectors: List<AbstractCollector>,
    val androidContext: Context
) {
    private val TAG = javaClass.simpleName

    fun start() {
        val intent = Intent(androidContext, CollectorService::class.java)
        ContextCompat.startForegroundService(androidContext, intent)
        Log.d(TAG, "start")
    }

    fun stop() {
        val intent = Intent(androidContext, CollectorService::class.java)

        androidContext.stopService(intent)
        collectors.onEach {
            it.stop()
        }
        Log.d(TAG, "stop")
    }
}