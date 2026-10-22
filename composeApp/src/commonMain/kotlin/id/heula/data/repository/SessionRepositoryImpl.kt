package id.heula.data.repository

import com.russhwolf.settings.Settings
import id.heula.domain.model.FastingSession
import id.heula.domain.model.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SessionRepositoryImpl(
    private val settings: Settings = Settings(),
) : SessionRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val key = "heula_fasting_sessions"

    private val _flow = MutableStateFlow(loadSessions())

    private fun loadSessions(): List<FastingSession> {
        val raw = settings.getStringOrNull(key) ?: return emptyList()
        return try {
            json.decodeFromString<List<FastingSession>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun persist(list: List<FastingSession>) {
        val serialized = json.encodeToString(list)
        settings.putString(key, serialized)
        _flow.value = list
    }

    override fun observeAll(): Flow<List<FastingSession>> = _flow.asStateFlow()

    override suspend fun add(session: FastingSession): Long {
        val current = _flow.value.toMutableList()
        val nextId = (current.maxOfOrNull { it.id } ?: 0L) + 1L
        val saved = session.copy(id = nextId)
        current.add(0, saved)
        persist(current)
        return nextId
    }

    override suspend fun delete(id: Long): Boolean {
        val current = _flow.value.toMutableList()
        val removed = current.removeAll { it.id == id }
        if (removed) persist(current)
        return removed
    }

    override suspend fun setMoodNote(id: Long, mood: Mood?, note: String?): Boolean {
        val current = _flow.value.toMutableList()
        val idx = current.indexOfFirst { it.id == id }
        if (idx >= 0) {
            current[idx] = current[idx].copy(mood = mood, note = note)
            persist(current)
            return true
        }
        return false
    }

    override suspend fun clearAll() {
        persist(emptyList())
    }
}
