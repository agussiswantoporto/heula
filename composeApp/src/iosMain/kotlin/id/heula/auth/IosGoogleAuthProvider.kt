package id.heula.auth

import id.heula.domain.model.UserAccount
import id.heula.domain.repository.GoogleAuthProvider

class IosGoogleAuthProvider : GoogleAuthProvider {
    override suspend fun signIn(): Result<UserAccount> {
        return Result.failure(Exception("Google Sign-In iOS dalam pengembangan."))
    }

    override suspend fun signOut(): Result<Unit> {
        return Result.success(Unit)
    }
}
