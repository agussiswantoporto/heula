package id.heula.data.repository

import id.heula.domain.model.WeightRecord
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    fun observeAll(): Flow<List<WeightRecord>>
    suspend fun add(record: WeightRecord): Long
    suspend fun delete(id: Long): Boolean
    suspend fun clearAll()
    suspend fun replaceAll(records: List<WeightRecord>)
    fun getDeletedTimestamps(): Set<Long>
    suspend fun markDeleted(timestamps: Collection<Long>)
}
