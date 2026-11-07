package id.heula.auth

import android.content.Context
import id.heula.domain.model.UserAccount
import id.heula.domain.repository.GoogleAuthProvider

class AndroidGoogleAuthProvider(
    private val context: Context,
) : GoogleAuthProvider {

    val webClientId = "60875023761-ph4ha1o3p9anq05qikk5vgl2j3ct6opj.apps.googleusercontent.com"

    override suspend fun signIn(): Result<UserAccount> {
        return Result.failure(Exception("Menunggu registrasi SHA-1 Android di Google Cloud Console."))
    }

    override suspend fun signOut(): Result<Unit> {
        return Result.success(Unit)
    }
}
