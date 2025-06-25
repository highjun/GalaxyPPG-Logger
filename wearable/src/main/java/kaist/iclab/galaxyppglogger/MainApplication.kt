package kaist.iclab.galaxyppglogger

import android.app.Application
import kaist.iclab.galaxyppglogger.data.HealthTrackerService
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            androidLogger(level = Level.NONE)
            modules(koinModule)
        }
        get<HealthTrackerService>().start()
    }
}