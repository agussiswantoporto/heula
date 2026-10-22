package id.heula.data.repository

import id.heula.domain.model.WeightRecord
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    fun observeAll(): Flow<List<WeightRecord>>
    suspend fun add(record: WeightRecord): Long
    suspend fun delete(id: Long): Boolean
    suspend fun clearAll()
}
