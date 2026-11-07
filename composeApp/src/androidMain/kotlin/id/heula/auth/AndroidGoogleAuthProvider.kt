package id.heula.auth

import android.content.Context
import id.heula.domain.model.UserAccount
import id.heula.domain.repository.GoogleAuthProvider

class AndroidGoogleAuthProvider(
    private val context: Context,
) : GoogleAuthProvider {

    val webClientId = "60875023761-qgh2fpt5q2a2ohbup77trk36s2tog0ic.apps.googleusercontent.com"

    override suspend fun signIn(): Result<UserAccount> {
        return Result.failure(Exception("Menunggu registrasi SHA-1 Android di Google Cloud Console."))
    }

    override suspend fun signOut(): Result<Unit> {
        return Result.success(Unit)
    }
}
