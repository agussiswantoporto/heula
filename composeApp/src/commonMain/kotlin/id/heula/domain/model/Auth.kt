package id.heula.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAccount(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
)

enum class SyncState {
    IDLE,
    SYNCING,
    SYNCED,
    FAILED,
}
