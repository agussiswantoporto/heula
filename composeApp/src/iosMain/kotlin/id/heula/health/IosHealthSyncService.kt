package id.heula.health

import id.heula.domain.model.HealthPermissionState
import id.heula.domain.model.HealthPlatformType
import id.heula.domain.model.HealthSyncStatus
import id.heula.domain.model.WeightRecord as DomainWeightRecord
import id.heula.domain.repository.HealthSyncService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970
import platform.HealthKit.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCancellableCoroutine

class IosHealthSyncService : HealthSyncService {

    private val healthStore: HKHealthStore? = if (HKHealthStore.isHealthDataAvailable()) HKHealthStore() else null

    private val _status = MutableStateFlow(
        HealthSyncStatus(
            platformType = HealthPlatformType.APPLE_HEALTH,
            isAvailable = HKHealthStore.isHealthDataAvailable(),
            state = if (HKHealthStore.isHealthDataAvailable()) HealthPermissionState.NEED_AUTHORIZATION else HealthPermissionState.NOT_AVAILABLE,
        )
    )
    override val status: StateFlow<HealthSyncStatus> = _status.asStateFlow()

    override suspend fun checkStatus(): HealthSyncStatus {
        if (healthStore == null) {
            val s = HealthSyncStatus(
                platformType = HealthPlatformType.APPLE_HEALTH,
                isAvailable = false,
                state = HealthPermissionState.NOT_AVAILABLE,
                errorMessage = "Apple Health tidak tersedia di perangkat ini"
            )
            _status.value = s
            return s
        }
        return _status.value
    }

    override suspend fun requestAuthorization(): Result<Boolean> {
        val store = healthStore ?: return Result.failure(Exception("Apple Health tidak tersedia"))

        val bodyMassType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)
        val waterType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryWater)

        val writeTypes = setOfNotNull(bodyMassType, waterType)
        val readTypes = setOfNotNull(bodyMassType, waterType)

        return suspendCancellableCoroutine { continuation ->
            store.requestAuthorizationToShareTypes(
                typesToShare = writeTypes,
                readTypes = readTypes,
            ) { success, error ->
                if (success) {
                    _status.value = _status.value.copy(
                        state = HealthPermissionState.CONNECTED,
                        isAvailable = true,
                    )
                    continuation.resume(Result.success(true))
                } else {
                    val errMsg = error?.localizedDescription ?: "Gagal mendapatkan izin Apple Health"
                    _status.value = _status.value.copy(
                        state = HealthPermissionState.ERROR,
                        errorMessage = errMsg,
                    )
                    continuation.resume(Result.failure(Exception(errMsg)))
                }
            }
        }
    }

    override suspend fun syncWeight(weightKg: Double, timestampMs: Long): Result<Unit> {
        val store = healthStore ?: return Result.failure(Exception("Apple Health tidak tersedia"))
        val bodyMassType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)
            ?: return Result.failure(Exception("Tipe data berat badan tidak ditemukan di iOS"))

        val unit = HKUnit.unitFromString("kg")
        val quantity = HKQuantity.quantityWithUnit(unit, weightKg)
        val date = NSDate.dateWithTimeIntervalSince1970(timestampMs / 1000.0)

        val sample = HKQuantitySample.quantitySampleWithType(
            quantityType = bodyMassType,
            quantity = quantity,
            startDate = date,
            endDate = date,
        )

        return suspendCancellableCoroutine { continuation ->
            store.saveObject(sample) { success, error ->
                if (success) {
                    _status.value = _status.value.copy(
                        state = HealthPermissionState.CONNECTED,
                        lastSyncedAt = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
                    )
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(Exception(error?.localizedDescription ?: "Gagal menyimpan ke Apple Health")))
                }
            }
        }
    }

    override suspend fun syncHydration(waterMl: Double, timestampMs: Long): Result<Unit> {
        val store = healthStore ?: return Result.failure(Exception("Apple Health tidak tersedia"))
        val waterType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryWater)
            ?: return Result.failure(Exception("Tipe data hidrasi tidak ditemukan di iOS"))

        val unit = HKUnit.unitFromString("mL")
        val quantity = HKQuantity.quantityWithUnit(unit, waterMl)
        val date = NSDate.dateWithTimeIntervalSince1970(timestampMs / 1000.0)

        val sample = HKQuantitySample.quantitySampleWithType(
            quantityType = waterType,
            quantity = quantity,
            startDate = date,
            endDate = date,
        )

        return suspendCancellableCoroutine { continuation ->
            store.saveObject(sample) { success, error ->
                if (success) {
                    _status.value = _status.value.copy(
                        state = HealthPermissionState.CONNECTED,
                        lastSyncedAt = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
                    )
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(Exception(error?.localizedDescription ?: "Gagal menyimpan air ke Apple Health")))
                }
            }
        }
    }

    override suspend fun syncAll(weights: List<DomainWeightRecord>, waterIntakeMl: Int): Result<Unit> {
        for (w in weights) {
            syncWeight(w.weightKg, w.recordedAt)
        }
        if (waterIntakeMl > 0) {
            syncHydration(waterIntakeMl.toDouble(), (NSDate().timeIntervalSince1970 * 1000.0).toLong())
        }
        return Result.success(Unit)
    }
}
