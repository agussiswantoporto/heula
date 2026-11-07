package id.heula.domain.repository

import id.heula.domain.model.UserAccount

interface GoogleAuthProvider {
    suspend fun signIn(): Result<UserAccount>
    suspend fun signOut(): Result<Unit>
}
