package id.heula.domain.repository

import id.heula.domain.model.SyncState
import id.heula.domain.model.UserAccount
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<UserAccount?>
    val syncState: StateFlow<SyncState>

    suspend fun signInWithGoogle(): Result<UserAccount>
    suspend fun signOut(): Result<Unit>
    suspend fun syncAllData(): Result<Unit>
}
