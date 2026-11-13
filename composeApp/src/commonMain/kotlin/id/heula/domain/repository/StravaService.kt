package id.heula.domain.repository

import id.heula.domain.model.FastingSession
import id.heula.domain.model.StravaAthlete
import id.heula.domain.model.StravaActivityResult
import id.heula.domain.model.StravaConfig

interface StravaService {
    fun getAuthorizationUrl(clientId: String, redirectUri: String): String

    suspend fun exchangeAuthorizationCode(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<StravaConfig>

    suspend fun refreshToken(config: StravaConfig): Result<StravaConfig>

    suspend fun verifyToken(accessToken: String): Result<StravaAthlete>

    suspend fun postActivity(
        config: StravaConfig,
        session: FastingSession,
    ): Result<StravaActivityResult>

    fun getDefaultRedirectUri(): String = "https://www.heula.website/"

    fun getDefaultCallbackDomain(): String = "heula.website"

    suspend fun getPendingOAuthCode(): String? = null

    fun clearPendingOAuthCode() {}

    fun launchOAuthFlow(clientId: String, redirectUri: String): Boolean = false
}
