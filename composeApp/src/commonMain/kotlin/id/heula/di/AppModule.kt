package id.heula.di

import com.russhwolf.settings.Settings
import id.heula.data.repository.AuthRepositoryImpl
import id.heula.data.repository.SessionRepository
import id.heula.data.repository.SessionRepositoryImpl
import id.heula.data.repository.SettingsRepository
import id.heula.data.repository.SettingsRepositoryImpl
import id.heula.data.repository.WeightRepository
import id.heula.data.repository.WeightRepositoryImpl
import id.heula.domain.repository.AuthRepository
import id.heula.domain.usecase.SyncManager
import id.heula.presentation.store.AppStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    includes(platformModule)
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<SessionRepository> { SessionRepositoryImpl() }
    single<WeightRepository> { WeightRepositoryImpl() }
    single {
        SyncManager(
            sessionRepository = get(),
            weightRepository = get(),
            settingsRepository = get(),
            cloudSyncService = get(),
        )
    }
    single<AuthRepository> {
        AuthRepositoryImpl(
            googleAuthProvider = get(),
            syncManager = get(),
            settings = Settings(),
        )
    }
    single {
        AppStore(
            settingsRepository = get(),
            sessionRepository = get(),
            weightRepository = get(),
            authRepository = get(),
            healthSyncService = get(),
            shareExporter = get(),
            scope = get(),
        )
    }
}
