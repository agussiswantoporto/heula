package id.heula.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume
import id.heula.domain.model.HealthPermissionState
import id.heula.domain.model.HealthPlatformType
import id.heula.domain.model.HealthSyncStatus
import id.heula.domain.model.WeightRecord as DomainWeightRecord
import id.heula.domain.repository.HealthSyncService
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AndroidHealthSyncService(
    private val context: Context,
) : HealthSyncService {

    private val permissions = setOf(
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class),
        HealthPermission.getReadPermission(HydrationRecord::class),
        HealthPermission.getWritePermission(HydrationRecord::class),
    )

    private val _status = MutableStateFlow(
        HealthSyncStatus(
            platformType = HealthPlatformType.HEALTH_CONNECT,
            isAvailable = false,
            state = HealthPermissionState.NOT_AVAILABLE,
        )
    )
    override val status: StateFlow<HealthSyncStatus> = _status.asStateFlow()

    private fun getClient(): HealthConnectClient? {
        val availability = HealthConnectClient.getSdkStatus(context)
        if (availability != HealthConnectClient.SDK_AVAILABLE) {
            return null
        }
        return HealthConnectClient.getOrCreate(context)
    }

    override suspend fun checkStatus(): HealthSyncStatus = withContext(Dispatchers.IO) {
        val client = getClient()
        if (client == null) {
            val s = HealthSyncStatus(
                platformType = HealthPlatformType.HEALTH_CONNECT,
                isAvailable = false,
                state = HealthPermissionState.NOT_AVAILABLE,
                errorMessage = "Health Connect tidak tersedia atau perlu diupdate di Google Play Store"
            )
            _status.value = s
            return@withContext s
        }

        try {
            val granted = client.permissionController.getGrantedPermissions()
            val isAllGranted = granted.containsAll(permissions)
            val s = HealthSyncStatus(
                platformType = HealthPlatformType.HEALTH_CONNECT,
                isAvailable = true,
                state = if (isAllGranted) HealthPermissionState.CONNECTED else HealthPermissionState.NEED_AUTHORIZATION,
                lastSyncedAt = _status.value.lastSyncedAt
            )
            _status.value = s
            s
        } catch (e: Exception) {
            val s = HealthSyncStatus(
                platformType = HealthPlatformType.HEALTH_CONNECT,
                isAvailable = true,
                state = HealthPermissionState.ERROR,
                errorMessage = e.message
            )
            _status.value = s
            s
        }
    }

    override suspend fun requestAuthorization(): Result<Boolean> = withContext(Dispatchers.IO) {
        val client = getClient() ?: return@withContext Result.failure(Exception("Health Connect tidak tersedia"))
        try {
            val granted = client.permissionController.getGrantedPermissions()
            val isAllGranted = granted.containsAll(permissions)
            if (isAllGranted) {
                _status.value = _status.value.copy(state = HealthPermissionState.CONNECTED)
                Result.success(true)
            } else {
                _status.value = _status.value.copy(state = HealthPermissionState.NEED_AUTHORIZATION)
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncWeight(weightKg: Double, timestampMs: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val client = getClient() ?: return@withContext Result.failure(Exception("Health Connect tidak tersedia"))
        try {
            val instant = Instant.ofEpochMilli(timestampMs)
            val zoneOffset = ZoneId.systemDefault().rules.getOffset(instant)
            val record = WeightRecord(
                time = instant,
                zoneOffset = zoneOffset,
                weight = Mass.kilograms(weightKg),
            )
            client.insertRecords(listOf(record))
            _status.value = _status.value.copy(
                state = HealthPermissionState.CONNECTED,
                lastSyncedAt = System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncHydration(waterMl: Double, timestampMs: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val client = getClient() ?: return@withContext Result.failure(Exception("Health Connect tidak tersedia"))
        try {
            val instant = Instant.ofEpochMilli(timestampMs)
            val zoneOffset = ZoneId.systemDefault().rules.getOffset(instant)
            val record = HydrationRecord(
                startTime = instant,
                startZoneOffset = zoneOffset,
                endTime = instant.plusSeconds(60),
                endZoneOffset = zoneOffset,
                volume = Volume.milliliters(waterMl),
            )
            client.insertRecords(listOf(record))
            _status.value = _status.value.copy(
                state = HealthPermissionState.CONNECTED,
                lastSyncedAt = System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncAll(weights: List<DomainWeightRecord>, waterIntakeMl: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val client = getClient() ?: return@withContext Result.failure(Exception("Health Connect tidak tersedia"))
        try {
            val records = mutableListOf<androidx.health.connect.client.records.Record>()
            for (w in weights) {
                val instant = Instant.ofEpochMilli(w.recordedAt)
                val zoneOffset = ZoneId.systemDefault().rules.getOffset(instant)
                records.add(
                    WeightRecord(
                        time = instant,
                        zoneOffset = zoneOffset,
                        weight = Mass.kilograms(w.weightKg),
                    )
                )
            }
            if (waterIntakeMl > 0) {
                val now = Instant.now()
                val zoneOffset = ZoneId.systemDefault().rules.getOffset(now)
                records.add(
                    HydrationRecord(
                        startTime = now.minusSeconds(60),
                        startZoneOffset = zoneOffset,
                        endTime = now,
                        endZoneOffset = zoneOffset,
                        volume = Volume.milliliters(waterIntakeMl.toDouble()),
                    )
                )
            }
            if (records.isNotEmpty()) {
                client.insertRecords(records)
            }
            _status.value = _status.value.copy(
                state = HealthPermissionState.CONNECTED,
                lastSyncedAt = System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
