package id.heula.auth

import id.heula.domain.model.UserAccount
import id.heula.domain.repository.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json

class WasmGoogleAuthProvider : GoogleAuthProvider {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun signIn(): Result<UserAccount> {
        clearWebGoogleAuthResult()
        triggerWebGoogleSignIn()

        val result = withTimeoutOrNull(60_000L) {
            while (true) {
                val raw = getWebGoogleAuthResult()
                if (raw.isNotEmpty()) {
                    return@withTimeoutOrNull raw
                }
                delay(300)
            }
            null
        }

        if (result == null) {
            return Result.failure(Exception("Waktu login Google habis atau pop-up ditutup."))
        }

        return try {
            val account = json.decodeFromString<UserAccount>(result)
            Result.success(account)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal membaca profil Google: ${e.message}"))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        clearWebGoogleAuthResult()
        triggerWebGoogleSignOut()
        return Result.success(Unit)
    }
}
