package id.heula.domain.repository

import id.heula.domain.model.HealthSyncStatus
import id.heula.domain.model.WeightRecord
import kotlinx.coroutines.flow.StateFlow

interface HealthSyncService {
    val status: StateFlow<HealthSyncStatus>
    suspend fun checkStatus(): HealthSyncStatus
    suspend fun requestAuthorization(): Result<Boolean>
    suspend fun syncWeight(weightKg: Double, timestampMs: Long): Result<Unit>
    suspend fun syncHydration(waterMl: Double, timestampMs: Long): Result<Unit>
    suspend fun syncAll(weights: List<WeightRecord>, waterIntakeMl: Int): Result<Unit>
}
