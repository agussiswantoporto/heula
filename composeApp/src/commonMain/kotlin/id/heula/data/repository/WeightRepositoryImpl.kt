package id.heula.data.repository

import com.russhwolf.settings.Settings
import id.heula.domain.model.WeightRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WeightRepositoryImpl(
    private val settings: Settings = Settings(),
) : WeightRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val key = "heula_weight_records"
    private val deletedKey = "heula_deleted_weights"

    private val _flow = MutableStateFlow(loadWeights())
    private var deletedTimestamps: MutableSet<Long> = loadDeletedTimestamps().toMutableSet()

    private fun loadWeights(): List<WeightRecord> {
        val raw = settings.getStringOrNull(key) ?: return emptyList()
        return try {
            json.decodeFromString<List<WeightRecord>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun loadDeletedTimestamps(): Set<Long> {
        val raw = settings.getStringOrNull(deletedKey) ?: return emptySet()
        return try {
            json.decodeFromString<Set<Long>>(raw)
        } catch (_: Exception) {
            emptySet()
        }
    }

    private fun persistDeleted(set: Set<Long>) {
        val trimmed = if (set.size > 500) set.toList().takeLast(500).toSet() else set
        deletedTimestamps = trimmed.toMutableSet()
        settings.putString(deletedKey, json.encodeToString(trimmed))
    }

    private fun persist(list: List<WeightRecord>) {
        val serialized = json.encodeToString(list)
        settings.putString(key, serialized)
        _flow.value = list
    }

    override fun observeAll(): Flow<List<WeightRecord>> = _flow.asStateFlow()

    override fun getDeletedTimestamps(): Set<Long> = deletedTimestamps.toSet()

    override suspend fun markDeleted(timestamps: Collection<Long>) {
        if (timestamps.isEmpty()) return
        val updated = deletedTimestamps.toMutableSet()
        updated.addAll(timestamps)
        persistDeleted(updated)
    }

    override suspend fun add(record: WeightRecord): Long {
        if (deletedTimestamps.contains(record.recordedAt)) {
            val updated = deletedTimestamps.toMutableSet()
            updated.remove(record.recordedAt)
            persistDeleted(updated)
        }
        val current = _flow.value.toMutableList()
        val nextId = (current.maxOfOrNull { it.id } ?: 0L) + 1L
        val saved = record.copy(id = nextId)
        current.add(saved)
        persist(current)
        return nextId
    }

    override suspend fun delete(id: Long): Boolean {
        val current = _flow.value.toMutableList()
        val toDelete = current.filter { it.id == id }
        if (toDelete.isNotEmpty()) {
            markDeleted(toDelete.map { it.recordedAt })
            current.removeAll { it.id == id }
            persist(current)
            return true
        }
        return false
    }

    override suspend fun clearAll() {
        persist(emptyList())
    }

    override suspend fun replaceAll(records: List<WeightRecord>) {
        persist(records)
    }
}
