package id.heula.data.repository

import id.heula.domain.usecase.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observe(): Flow<AppSettings>
    suspend fun save(settings: AppSettings)
}
