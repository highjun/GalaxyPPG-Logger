package kaist.iclab.galaxyppglogger.data

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kaist.iclab.galaxyppglogger.collector.ACC.AccEntity
import kaist.iclab.galaxyppglogger.collector.BaseDao
import kaist.iclab.galaxyppglogger.collector.HR.HREntity
import kaist.iclab.galaxyppglogger.collector.PPG.PpgEntity
import kaist.iclab.galaxyppglogger.collector.SkinTemp.SkinTempEntity
import kaist.iclab.galaxyppglogger.showToastFromBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.OutputStream

class PhoneCommunicationManager(
    private val androidContext: Context,
    private val daos: Map<String, BaseDao<*>>,
) {
    private val TAG = javaClass.simpleName
    private val dataClient = Wearable.getDataClient(androidContext)
    private val capabilityClient = Wearable.getCapabilityClient(androidContext)
    private val channelClient = Wearable.getChannelClient(androidContext)
    private val capabilityName = "PPG_LOGGER"

    private val _localNodeInfo = MutableStateFlow<NodeInfo?>(null)
    val localNodeInfo: StateFlow<NodeInfo?> = _localNodeInfo.asStateFlow()

    init {
        capabilityClient.removeLocalCapability(capabilityName).addOnSuccessListener {
            capabilityClient.addLocalCapability(capabilityName)
                .addOnSuccessListener {
                    Log.d(TAG, "Capability Registered: $capabilityName")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Capability Register Failed", it)
                }
        }
        CoroutineScope(Dispatchers.IO).launch {
            getLocalNodeInfo(androidContext).let { nodeInfo ->
                _localNodeInfo.value = nodeInfo
                Log.d(TAG, "Local Node Info: ${nodeInfo.name} (${nodeInfo.id})")
            }
        }
    }

    suspend fun getLocalNodeInfo(context: Context): NodeInfo {
        Log.d(TAG, "getLocalNodeInfo")
        val localNode = Wearable.getNodeClient(context).localNode.await()
        return NodeInfo(
            name = localNode.displayName,
            id = localNode.id
        )
    }

    fun sendStat() {
        Log.d(TAG, "sendData2Phone")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val timestamp = System.currentTimeMillis()
                val acc = (daos["acc"]!!.getLast() as AccEntity)
                val ppg = daos["ppg"]!!.getLast() as PpgEntity
                val hr = daos["hr"]!!.getLast() as HREntity
                val request = PutDataMapRequest.create("/STAT").apply {
                    dataMap.putLong("timestamp", timestamp)
                    dataMap.putInt("accX", acc.x)
                    dataMap.putInt("accY", acc.y)
                    dataMap.putInt("accZ", acc.z)
                    dataMap.putInt("ppg", ppg.green)
                    dataMap.putInt("ppgStatus", ppg.greenStatus)
                    dataMap.putInt("hr", hr.hr)
                    dataMap.putInt("hrStatus", hr.hrStatus)
                }.asPutDataRequest()
                dataClient.putDataItem(request).await()
                Log.d(TAG, "Stat has been uploaded")
            } catch (exception: Exception) {
                Log.e(TAG, "Saving DataItem failed: $exception")
            }
        }
    }

    fun findPhoneAndSendFile() {
        showToastFromBackground(androidContext, "Sending Data. Please wait for a while...")

        capabilityClient.getCapability(capabilityName, CapabilityClient.FILTER_REACHABLE)
            .addOnSuccessListener { capabilityInfo ->
                val node = capabilityInfo.nodes.firstOrNull()
                if (node != null) {
                    openChannelAndSendFile(node.id)
                } else {
                    Log.e(TAG, "No Smartphone Node")
                    showToastFromBackground(androidContext, "Error - No Smartphone found")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Node Search Failure", it)
                showToastFromBackground(androidContext, "Error - No Smartphone found")
            }
    }

    fun openChannelAndSendFile(nodeId: String) {

        channelClient.openChannel(nodeId, "file-transfer")
            .addOnSuccessListener { channel ->
                channelClient
                    .getOutputStream(channel)
                    .addOnSuccessListener { outputStream ->
                        Log.d(TAG, "Channel Opened: ${channel.nodeId}")
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                Log.d(TAG, "Sending file to smartphone")
                                outputStream.use { sendFile(it) }
                                Log.d(TAG, "File sent successfully")
                                showToastFromBackground(androidContext, "Data Sent successfully")
                            } catch (e: Exception) {
                                Log.e(TAG, "File sending failed", e)
                                showToastFromBackground(androidContext, "Error - Phone app should be running")
                            }

                        }
                    }
            }
            .addOnFailureListener {
                Log.e(TAG, "Channel Open Failure", it)
            }

    }

    suspend fun sendFile(outputStream: OutputStream) {
        val csvBuilder = StringBuilder()
        for ((name, dao) in daos) {
            Log.d(TAG, "Generate CSV data from DAO: $name")
            when (name) {
                "acc" -> {
                    csvBuilder.append("id,dataReceived,timestamp,x,y,z\n")
                    val entries = (dao.getAll() as List<AccEntity>)
                    for (entry in entries) {
                        csvBuilder.append("${entry.id},${entry.dataReceived},${entry.timestamp},${entry.x},${entry.y},${entry.z}\n")
                    }
                }

                "ppg" -> {
                    csvBuilder.append("id,dataReceived,timestamp,green,greenStatus,red,redStatus,ir,irStatus\n")
                    val entries = (dao.getAll() as List<PpgEntity>)
                    for (entry in entries) {
                        csvBuilder.append("${entry.id},${entry.dataReceived},${entry.timestamp},")
                        csvBuilder.append("${entry.green},${entry.greenStatus},${entry.red},${entry.redStatus},${entry.ir},${entry.irStatus}\n")
                    }
                }

                "hr" -> {
                    csvBuilder.append("id,dataReceived,timestamp,hr,hrStatus,ibi,ibiStatus\n")
                    val entries = (dao.getAll() as List<HREntity>)
                    for (entry in entries) {
                        csvBuilder.append("${entry.id},${entry.dataReceived},${entry.timestamp},")
                        csvBuilder.append("${entry.hr},${entry.hrStatus},${entry.ibi},${entry.ibiStatus}\n")
                    }
                }

                "skin-temp" -> {
                    csvBuilder.append("id,dataReceived,timestamp,ambientTemp,objectTemp,status\n")
                    val entries = (dao.getAll() as List<SkinTempEntity>)
                    for (entry in entries) {
                        csvBuilder.append("${entry.id},${entry.dataReceived},${entry.timestamp},")
                        csvBuilder.append("${entry.ambientTemp},${entry.objectTemp},${entry.status}\n")
                    }
                }
            }
            csvBuilder.append("\n\n") // Add a separator between different DAOs
        }
        outputStream.writer(Charsets.UTF_8).use {
            it.write(csvBuilder.toString())
        }
    }
}