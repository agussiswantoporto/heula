package id.heula.di

import id.heula.data.repository.SessionRepository
import id.heula.data.repository.SessionRepositoryImpl
import id.heula.data.repository.SettingsRepository
import id.heula.data.repository.SettingsRepositoryImpl
import id.heula.data.repository.WeightRepository
import id.heula.data.repository.WeightRepositoryImpl
import id.heula.presentation.store.AppStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<SessionRepository> { SessionRepositoryImpl() }
    single<WeightRepository> { WeightRepositoryImpl() }
    single {
        AppStore(
            settingsRepository = get(),
            sessionRepository = get(),
            weightRepository = get(),
            scope = get(),
        )
    }
}
