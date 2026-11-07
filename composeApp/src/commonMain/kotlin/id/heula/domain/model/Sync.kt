package id.heula.domain.model

import id.heula.domain.usecase.AppSettings
import kotlinx.serialization.Serializable

@Serializable
data class UserCloudData(
    val lastSyncedAt: Long = 0L,
    val sessions: List<FastingSession> = emptyList(),
    val weights: List<WeightRecord> = emptyList(),
    val settings: AppSettings = AppSettings(),
)
