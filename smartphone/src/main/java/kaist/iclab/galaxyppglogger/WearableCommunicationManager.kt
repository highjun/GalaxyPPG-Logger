package kaist.iclab.galaxyppglogger

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kaist.iclab.galaxyppglogger.data.WearableNodeInfo
import kaist.iclab.galaxyppglogger.data.WearableStatDao
import kaist.iclab.galaxyppglogger.data.WearableStatEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File


class WearableCommunicationManager(
    val wearableStatDao: WearableStatDao,
    val context: Context
) : DataClient.OnDataChangedListener,
    CapabilityClient.OnCapabilityChangedListener,
    ChannelClient.ChannelCallback() {
    private val TAG = javaClass.simpleName

    private val _connectedNodeInfo = MutableStateFlow<WearableNodeInfo?>(null)
    val connectedNodeInfo: StateFlow<WearableNodeInfo?> get() = _connectedNodeInfo

    private val capabilityClient = Wearable.getCapabilityClient(context)
    private val capabilityName = "PPG_LOGGER"

    init {
        // 초기 확인
        checkInitialConnection()
        // 로컬 캐패빌리티 등록
        capabilityClient.removeLocalCapability(capabilityName).addOnSuccessListener {
            capabilityClient.addLocalCapability(capabilityName)
                .addOnSuccessListener {
                    Log.d(TAG, "Capability Registered: $capabilityName")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Capability Register Failed", it)
                }
        }
    }

    fun init() {
        Wearable.getDataClient(context).addListener(this)
        Wearable.getChannelClient(context).registerChannelCallback(this)
        Wearable.getCapabilityClient(context).addListener(
            this,
            capabilityName
        )
    }

    fun clear() {
        Wearable.getDataClient(context).removeListener(this)
        Wearable.getChannelClient(context).unregisterChannelCallback(this)
        Wearable.getCapabilityClient(context).removeListener(
            this,
            capabilityName
        )
    }

    private fun checkInitialConnection() {
        capabilityClient.getCapability(capabilityName, CapabilityClient.FILTER_REACHABLE)
            .addOnSuccessListener { capabilityInfo ->
                updateStateFromNodes(capabilityInfo.nodes)
            }
            .addOnFailureListener {
                _connectedNodeInfo.value = null
            }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        updateStateFromNodes(capabilityInfo.nodes)
    }

    private fun updateStateFromNodes(nodes: Set<Node>) {
        val bestNode = nodes.firstOrNull()
        _connectedNodeInfo.value = bestNode?.let {
            WearableNodeInfo(
                nodeId = it.id,
                displayName = it.displayName
            )
        }
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        for (event in dataEventBuffer) {
            if (event.dataItem.uri.path == "/STAT") {
                val data = DataMapItem.fromDataItem(event.dataItem).dataMap
                val scale = 1 / (16383.75 / 4.0)
                /* Assume that data is always below keys*/
                CoroutineScope(Dispatchers.IO).launch {
                    wearableStatDao.insertEvents(
                        listOf(
                            WearableStatEntity(
                                timestamp = data.getLong("timestamp"),

                                accX = data.getInt("accX") * scale.toFloat(),
                                accY = data.getInt("accY") * scale.toFloat(),
                                accZ = data.getInt("accZ") * scale.toFloat(),

                                ppg = data.getInt("ppg"),
                                ppgStatus = data.getInt("ppgStatus"),

                                hr = data.getInt("hr"),
                                hrStatus = data.getInt("hrStatus")
                            )
                        )
                    )
                }
            } else {
                Log.e(TAG, "onDataChanged - Unhandled data item: ${event.dataItem.uri.path}")
            }
        }
    }

    override fun onChannelOpened(channel: ChannelClient.Channel) {
        val channelClient = Wearable.getChannelClient(context)
        channelClient.getInputStream(channel)
            .addOnSuccessListener { inputStream ->
                val file = File(context.filesDir, "tmp.dat")
                file.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                Log.d(TAG, "file Received - ${file.absolutePath}")
                showToastFromBackground(context, "Data Received from Wearable")
            }
            .addOnFailureListener {
                Log.e(TAG, "Input Stream Failed", it)
            }
    }
}


