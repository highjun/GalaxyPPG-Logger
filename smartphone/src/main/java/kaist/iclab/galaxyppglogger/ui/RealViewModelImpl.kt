package kaist.iclab.galaxyppglogger.ui

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.*
import android.util.Log
import androidx.lifecycle.viewModelScope
import kaist.iclab.galaxyppglogger.WearableCommunicationManager
import kaist.iclab.galaxyppglogger.data.EventDao
import kaist.iclab.galaxyppglogger.data.EventEntity
import kaist.iclab.galaxyppglogger.data.WearableNodeInfo
import kaist.iclab.galaxyppglogger.data.WearableStatDao
import kaist.iclab.galaxyppglogger.data.WearableStatEntity
import kaist.iclab.galaxyppglogger.showToastFromBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class RealViewModelImpl(
    private val context: Context,
    private val wearableStatDao: WearableStatDao,
    private val eventDao: EventDao,
    private val wearableCommunicationManager: WearableCommunicationManager
) : AbstractViewModel() {
    companion object {
        const val TAG = "RealMainViewModel"
    }

    override val connectedNodeInfo: StateFlow<WearableNodeInfo?>
        get() = wearableCommunicationManager.connectedNodeInfo

    override val wearableStatState: StateFlow<WearableStatEntity> = wearableStatDao.getLast()
        .map {
            Log.d("AbstractViewModel", "${System.currentTimeMillis()} WearableStatEntity: $it")
            return@map it ?: WearableStatEntity()
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = WearableStatEntity()
        )

    override val eventsState: StateFlow<List<EventEntity>> = eventDao.getAllEvent()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = listOf()
        )

    private var job: Job? = null
    override fun tag() {
        Log.d(TAG, "tag")
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.insertEvent(
                EventEntity(
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun writeFile(baseDir: String, fileName: String,  mimeType: String, bytes: ByteArray) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaColumns.DISPLAY_NAME, fileName) // 파일 이름
            put(MediaColumns.MIME_TYPE, mimeType)
            put(MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$baseDir")
        }
        resolver.insert(
            Files.getContentUri("external"),
            contentValues
        )?.let{
            resolver.openOutputStream(it)?.use {
                it.write(bytes)
                Log.d(TAG, "File $fileName written to $baseDir")
            } ?: run {
                Log.e(TAG, "Failed to open output stream for $fileName")
            }
        }
    }
    override fun export() {
        Log.d(TAG, "export")
        showToastFromBackground(context, "Export started. Please wait...")
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val folderName = "GalaxyPPGExport-${System.currentTimeMillis()}"
                writeFile(
                    baseDir = folderName,
                    fileName = ".folder_marker",
                    mimeType = "text/plain",
                    bytes = ByteArray(0) // 빈 파일로 폴더 마커 생성
                )
                exportWearableData(context, folderName)
                exportPhoneData(folderName)
                Log.d(TAG, "Export completed")
                showToastFromBackground(context, "Export completed into Download/$folderName")
            } catch (e: Exception) {
                Log.e(TAG, "Error exporting wearable data", e)
                showToastFromBackground(context, "Error exporting data. please retry")
            }

        }
    }

    override fun reset() {
        _isMonitoringState.value = false
        _lapsedTime.value = 0L
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.deleteAll()
            wearableStatDao.deleteAll()
        }
    }

    override fun stop() {
        _isMonitoringState.value = false
        job?.cancel()
    }

    override fun start() {
        _isMonitoringState.value = true
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isMonitoringState.value) {
                _lapsedTime.value += 1000L
                delay(1000L)
            }
        }
    }

    fun exportWearableData(context: Context, folderName: String) {

        val inputFile = File(context.filesDir, "tmp.dat")
        if (!inputFile.exists()) {
            Log.e("CSVParser", "No tmp.dat")
            return
        }
        val fullText = inputFile.readText(Charsets.UTF_8)
        val blocks = fullText.trim().split(Regex("\n{2,}"))

        blocks.forEachIndexed { index, block ->
            val fileName = when(index) {
                1 -> "ppg.csv"
                2 -> "acc.csv"
                0 -> "hr.csv"
                3 -> "skin_temp.csv"
                else -> "unknown_$index.csv"
            }
            writeFile(
                baseDir = folderName,
                fileName = fileName,
                mimeType = "text/csv",
                bytes = block.toByteArray(Charsets.UTF_8)
            )
        }
    }

    suspend fun exportPhoneData(
       baseDir: String
    ) {
        try {
            val events = eventDao.getAll()
            val csvBuilder = StringBuilder()
            csvBuilder.append("id,timestamp\n")
            for (event in events) {
                csvBuilder.append("${event.id},${event.timestamp}\n")
            }
            writeFile(
                baseDir = baseDir,
                fileName = "event.csv",
                mimeType = "text/csv",
                bytes = csvBuilder.toString().toByteArray(Charsets.UTF_8)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting phone data", e)
        }
    }
}