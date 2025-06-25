package kaist.iclab.galaxyppglogger.collector

interface BaseDao<T> {
    suspend fun insertEvents(events: List<T>)
    suspend fun deleteAll()
    suspend fun getAll(): List<T>
    suspend fun getLast(): T?
}