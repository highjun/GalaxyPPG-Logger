package kaist.iclab.galaxyppglogger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.galaxyppglogger.db.EventDao
import kaist.iclab.galaxyppglogger.db.EventEntity
import kaist.iclab.galaxyppglogger.db.RecentDao
import kaist.iclab.galaxyppglogger.db.RecentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    val eventDao: EventDao,
    val recentDao: RecentDao
) : ViewModel(){
    private val TAG = javaClass.simpleName

    fun onClick() {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.insertEvent(
                EventEntity(timestamp = System.currentTimeMillis())
            )
        }
    }

    val eventsState: StateFlow<List<EventEntity>> =
        eventDao.getAllEvent().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = listOf()
        )

    val recentDataState: StateFlow<RecentEntity?> =
        recentDao.getLastEvent().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RecentEntity(timestamp = -1, acc= "null", ppg="null", hr="null")
        )
}
