package id.heula.domain.model

enum class HealthPlatformType {
    HEALTH_CONNECT, // Android
    APPLE_HEALTH,   // iOS
    UNSUPPORTED     // Web / Desktop
}

enum class HealthPermissionState {
    NOT_AVAILABLE,
    NEED_AUTHORIZATION,
    CONNECTED,
    ERROR
}

data class HealthSyncStatus(
    val platformType: HealthPlatformType,
    val isAvailable: Boolean = false,
    val state: HealthPermissionState = HealthPermissionState.NOT_AVAILABLE,
    val lastSyncedAt: Long = 0L,
    val errorMessage: String? = null,
)
