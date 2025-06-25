package kaist.iclab.galaxyppglogger

import androidx.room.Room
import kaist.iclab.galaxyppglogger.data.RoomDB
import kaist.iclab.galaxyppglogger.ui.AbstractViewModel
import kaist.iclab.galaxyppglogger.ui.RealViewModelImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            RoomDB::class.java,
            "RoomDB"
        )
        .fallbackToDestructiveMigration(true) // For Dev Phase!
        .build()
    }
    single{
        get<RoomDB>().eventDao()
    }

    single {
        get<RoomDB>().wearableStatDao()
    }


    singleOf(::WearableCommunicationManager)
//    viewModel<AbstractViewModel>{
//        FakeViewModelImpl(get(), get())
//    }
    viewModel<AbstractViewModel>{
        RealViewModelImpl(get(), get(), get(), get())
    }
}