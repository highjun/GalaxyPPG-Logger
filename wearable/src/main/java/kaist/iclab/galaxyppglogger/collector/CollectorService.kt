package kaist.iclab.galaxyppglogger.collector

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kaist.iclab.galaxyppglogger.data.PhoneCommunicationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class CollectorService : Service() {
    private val collectorController by inject<CollectorController>()
    private val phoneCommunicationManager by inject<PhoneCommunicationManager>()
    private val TAG = javaClass.simpleName
    private val channelId = TAG
    private val channelName = "Galaxy PPG Logger"
    private val channelText = "We are collecting PPG data"

    private val binder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    inner class LocalBinder : Binder() {
        fun getService() = this@CollectorService
    }

    var job: Job? = null
    var isRunning = false

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                phoneCommunicationManager.sendStat()
                delay(TimeUnit.SECONDS.toMillis(10))
            }
        }
        collectorController.collectors.forEach {
            it.start()
        }
        isRunning = true

        createNotificationChannel()
        val notification: Notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle(channelName)
                .setContentText(channelText)
                .build()
        startForeground(1, notification)
        return START_STICKY
    }


    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        job?.cancel()
        job = null
        isRunning = false
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(channel)
    }
}