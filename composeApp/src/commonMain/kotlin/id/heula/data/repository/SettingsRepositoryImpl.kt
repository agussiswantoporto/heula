package id.heula.data.repository

import com.russhwolf.settings.Settings
import id.heula.domain.usecase.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsRepositoryImpl(
    private val settings: Settings = Settings(),
) : SettingsRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val key = "heula_app_settings"

    private val _flow = MutableStateFlow(loadSettings())

    private fun loadSettings(): AppSettings {
        val raw = settings.getStringOrNull(key) ?: return AppSettings()
        return try {
            json.decodeFromString<AppSettings>(raw)
        } catch (_: Exception) {
            AppSettings()
        }
    }

    override fun observe(): Flow<AppSettings> = _flow.asStateFlow()

    override suspend fun save(settings: AppSettings) {
        val serialized = json.encodeToString(settings)
        this.settings.putString(key, serialized)
        _flow.value = settings
    }
}
