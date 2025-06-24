package kaist.iclab.galaxyppglogger.collector

interface CollectorInterface {
    val TAG: String
    fun setup()
    suspend fun getStatus(): Boolean
    fun isAvailable():Boolean
    fun startLogging()
    fun stopLogging()
    suspend fun stringifyData(): String
    fun flush()
}