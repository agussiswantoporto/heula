package id.heula.data.repository

import com.russhwolf.settings.Settings
import id.heula.domain.model.SyncState
import id.heula.domain.model.UserAccount
import id.heula.domain.repository.AuthRepository
import id.heula.domain.repository.GoogleAuthProvider
import id.heula.domain.usecase.SyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val googleAuthProvider: GoogleAuthProvider,
    private val syncManager: SyncManager,
    private val settings: Settings = Settings(),
) : AuthRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val userKey = "heula_auth_user"
    private val syncTimeKey = "heula_last_synced_at"

    private val _currentUser = MutableStateFlow(loadCachedUser())
    override val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _syncState = MutableStateFlow(SyncState.IDLE)
    override val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _lastSyncedAt = MutableStateFlow(settings.getLongOrNull(syncTimeKey))
    override val lastSyncedAt: StateFlow<Long?> = _lastSyncedAt.asStateFlow()

    private fun loadCachedUser(): UserAccount? {
        val raw = settings.getStringOrNull(userKey) ?: return null
        return try {
            json.decodeFromString<UserAccount>(raw)
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun signInWithGoogle(): Result<UserAccount> {
        _syncState.value = SyncState.SYNCING
        val result = googleAuthProvider.signIn()
        return result.fold(
            onSuccess = { user ->
                settings.putString(userKey, json.encodeToString(user))
                _currentUser.value = user
                syncAllData()
                Result.success(user)
            },
            onFailure = { err ->
                _syncState.value = SyncState.FAILED
                Result.failure(err)
            }
        )
    }

    override suspend fun signOut(): Result<Unit> {
        val res = googleAuthProvider.signOut()
        settings.remove(userKey)
        settings.remove(syncTimeKey)
        _currentUser.value = null
        _lastSyncedAt.value = null
        _syncState.value = SyncState.IDLE
        return res
    }

    override suspend fun syncAllData(): Result<Unit> {
        val user = _currentUser.value
            ?: return Result.failure(IllegalStateException("Belum masuk ke akun Google"))

        _syncState.value = SyncState.SYNCING
        val syncResult = syncManager.sync(user)
        return syncResult.fold(
            onSuccess = { timestamp ->
                settings.putLong(syncTimeKey, timestamp)
                _lastSyncedAt.value = timestamp
                _syncState.value = SyncState.SYNCED
                Result.success(Unit)
            },
            onFailure = { err ->
                _syncState.value = SyncState.FAILED
                Result.failure(err)
            }
        )
    }
}
