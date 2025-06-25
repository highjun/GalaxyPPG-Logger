package kaist.iclab.galaxyppglogger

import androidx.room.Room
import kaist.iclab.galaxyppglogger.collector.ACC.ACCCollector
import kaist.iclab.galaxyppglogger.collector.ACC.AccDao
import kaist.iclab.galaxyppglogger.collector.BaseDao
import kaist.iclab.galaxyppglogger.collector.CollectorController
import kaist.iclab.galaxyppglogger.collector.HR.HRCollector
import kaist.iclab.galaxyppglogger.collector.HR.HRDao
import kaist.iclab.galaxyppglogger.collector.PPG.PPGCollector
import kaist.iclab.galaxyppglogger.collector.PPG.PpgDao
import kaist.iclab.galaxyppglogger.collector.SkinTemp.SkinTempCollector
import kaist.iclab.galaxyppglogger.collector.SkinTemp.SkinTempDao
import kaist.iclab.galaxyppglogger.data.PhoneCommunicationManager
import kaist.iclab.galaxyppglogger.data.HealthTrackerService
import kaist.iclab.galaxyppglogger.data.RoomDB
import kaist.iclab.galaxyppglogger.ui.AbstractViewModel
import kaist.iclab.galaxyppglogger.ui.RealViewModelImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val koinModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            RoomDB::class.java,
            "RoomDB"
        )
        .fallbackToDestructiveMigration(false) // For Dev Phase!
        .build()
    }

    single {
        get<RoomDB>().hrDao()
    }
    single {
        get<RoomDB>().ppgDao()
    }
    single {
        get<RoomDB>().accDao()
    }
    single {
        get<RoomDB>().skinTempDao()
    }

    singleOf(::HealthTrackerService)

    singleOf(::HRCollector)
    singleOf(::PPGCollector)
    singleOf(::ACCCollector)
    singleOf(::SkinTempCollector)

    single<Map<String, BaseDao<*>>>(named("daos")) {
        mapOf(
            "hr" to get<HRDao>(),
            "ppg" to get<PpgDao>(),
            "acc" to get<AccDao>(),
            "skin-temp" to get<SkinTempDao>()
        )
    }

    single{
        PhoneCommunicationManager(androidContext(), get(named("daos")))
    }

    single{
        CollectorController(
            collectors = listOf(
                get<HRCollector>(),
                get<PPGCollector>(),
                get<ACCCollector>(),
                get<SkinTempCollector>()
            ),
            androidContext = androidContext()
        )
    }

    viewModel<AbstractViewModel>{
        RealViewModelImpl(androidApplication(), get(),get(named("daos")), get())
//        FakeViewModelImpl(get())
    }
}