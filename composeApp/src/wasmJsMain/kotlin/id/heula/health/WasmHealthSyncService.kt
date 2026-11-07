package id.heula.health

import id.heula.domain.model.HealthPermissionState
import id.heula.domain.model.HealthPlatformType
import id.heula.domain.model.HealthSyncStatus
import id.heula.domain.model.WeightRecord
import id.heula.domain.repository.HealthSyncService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WasmHealthSyncService : HealthSyncService {
    private val _status = MutableStateFlow(
        HealthSyncStatus(
            platformType = HealthPlatformType.UNSUPPORTED,
            isAvailable = false,
            state = HealthPermissionState.NOT_AVAILABLE,
            errorMessage = "Health Connect & Apple Health tersedia di aplikasi Android & iOS",
        )
    )
    override val status: StateFlow<HealthSyncStatus> = _status.asStateFlow()

    override suspend fun checkStatus(): HealthSyncStatus = _status.value

    override suspend fun requestAuthorization(): Result<Boolean> {
        return Result.failure(Exception("Integrasi Health Connect & Apple Health berjalan di aplikasi Android & iOS."))
    }

    override suspend fun syncWeight(weightKg: Double, timestampMs: Long): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun syncHydration(waterMl: Double, timestampMs: Long): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun syncAll(weights: List<WeightRecord>, waterIntakeMl: Int): Result<Unit> {
        return Result.success(Unit)
    }
}
