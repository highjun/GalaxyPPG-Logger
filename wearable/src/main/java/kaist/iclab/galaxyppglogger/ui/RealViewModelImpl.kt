package kaist.iclab.galaxyppglogger.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import kaist.iclab.galaxyppglogger.collector.BaseDao
import kaist.iclab.galaxyppglogger.collector.CollectorController
import kaist.iclab.galaxyppglogger.collector.CollectorService
import kaist.iclab.galaxyppglogger.data.PhoneCommunicationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RealViewModelImpl(
    private val application: Application,
    private val collectorController: CollectorController,
    private val daos: Map<String, BaseDao<*>>,
    private val phoneCommunicationManager: PhoneCommunicationManager
) : AbstractViewModel() {
    private val TAG = javaClass.simpleName

    private val _serviceState = MutableStateFlow(ServiceState.DISCONNECTED)
    override val serviceState
        get() = _serviceState.asStateFlow()

    override val localNodeInfo
        get() = phoneCommunicationManager.localNodeInfo

    /* start collecting data */
    override fun start() {
        Log.d(TAG, "start")
        collectorController.start()
        _serviceState.value = ServiceState.RUNNING
    }

    /* stop collecting data */
    override fun stop() {
        Log.d(TAG, "stop")
        collectorController.stop()
        _serviceState.value = ServiceState.READY
        unbindService()
    }

    override fun export() {
        Log.d(TAG, "export")
        phoneCommunicationManager.findPhoneAndSendFile()
    }

    /* delete all data */
    override fun flush() {
        CoroutineScope(Dispatchers.IO).launch {
            daos.forEach { (_, dao) ->
                dao.deleteAll()
            }
        }
        Log.d(TAG, "flush")
    }

    private var serviceBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if((binder as CollectorService.LocalBinder).getService().isRunning){
                _serviceState.value = ServiceState.RUNNING
            } else {
                _serviceState.value = ServiceState.READY
            }
            serviceBound = true
            Log.d(TAG, "Service Binded")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            _serviceState.value = ServiceState.DISCONNECTED
            Log.d(TAG, "Service Disconnected")
        }
    }

    override fun bindService() {
        val context = application.applicationContext
        val intent = Intent(context, CollectorService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun unbindService() {
        if (serviceBound) {
            application.applicationContext.unbindService(connection)
            serviceBound = false
        }
    }

}