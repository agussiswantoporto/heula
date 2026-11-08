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
    private val deletedKey = "heula_deleted_sessions"

    private val _flow = MutableStateFlow(loadSessions())
    private var deletedStartMs: MutableSet<Long> = loadDeletedStartMs().toMutableSet()

    private fun loadSessions(): List<FastingSession> {
        val raw = settings.getStringOrNull(key) ?: return emptyList()
        return try {
            json.decodeFromString<List<FastingSession>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun loadDeletedStartMs(): Set<Long> {
        val raw = settings.getStringOrNull(deletedKey) ?: return emptySet()
        return try {
            json.decodeFromString<Set<Long>>(raw)
        } catch (_: Exception) {
            emptySet()
        }
    }

    private fun persistDeleted(set: Set<Long>) {
        val trimmed = if (set.size > 500) set.toList().takeLast(500).toSet() else set
        deletedStartMs = trimmed.toMutableSet()
        settings.putString(deletedKey, json.encodeToString(trimmed))
    }

    private fun persist(list: List<FastingSession>) {
        val serialized = json.encodeToString(list)
        settings.putString(key, serialized)
        _flow.value = list
    }

    override fun observeAll(): Flow<List<FastingSession>> = _flow.asStateFlow()

    override fun getDeletedStartMs(): Set<Long> = deletedStartMs.toSet()

    override suspend fun markDeleted(startMsList: Collection<Long>) {
        if (startMsList.isEmpty()) return
        val updated = deletedStartMs.toMutableSet()
        updated.addAll(startMsList)
        persistDeleted(updated)
    }

    override suspend fun add(session: FastingSession): Long {
        if (deletedStartMs.contains(session.startMs)) {
            val updated = deletedStartMs.toMutableSet()
            updated.remove(session.startMs)
            persistDeleted(updated)
        }
        val current = _flow.value.toMutableList()
        val nextId = (current.maxOfOrNull { it.id } ?: 0L) + 1L
        val saved = session.copy(id = nextId)
        current.add(0, saved)
        persist(current)
        return nextId
    }

    override suspend fun delete(id: Long): Boolean {
        val current = _flow.value.toMutableList()
        val toDelete = current.filter { it.id == id }
        if (toDelete.isNotEmpty()) {
            markDeleted(toDelete.map { it.startMs })
            current.removeAll { it.id == id }
            persist(current)
            return true
        }
        return false
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

    override suspend fun update(session: FastingSession): Boolean {
        val current = _flow.value.toMutableList()
        val idx = current.indexOfFirst { it.id == session.id }
        if (idx >= 0) {
            val oldSession = current[idx]
            if (oldSession.startMs != session.startMs) {
                markDeleted(listOf(oldSession.startMs))
            }
            if (deletedStartMs.contains(session.startMs)) {
                val updated = deletedStartMs.toMutableSet()
                updated.remove(session.startMs)
                persistDeleted(updated)
            }
            current[idx] = session
            persist(current)
            return true
        }
        return false
    }

    override suspend fun clearAll() {
        persist(emptyList())
    }

    override suspend fun replaceAll(sessions: List<FastingSession>) {
        persist(sessions)
    }
}
