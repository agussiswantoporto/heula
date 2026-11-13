package id.heula.strava

import id.heula.domain.model.FastingSession
import id.heula.domain.model.StravaAthlete
import id.heula.domain.model.StravaActivityResult
import id.heula.domain.model.StravaConfig
import id.heula.domain.repository.StravaService

class IosStravaService : StravaService {
    override fun getAuthorizationUrl(clientId: String, redirectUri: String): String {
        return "https://www.strava.com/oauth/authorize?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&approval_prompt=auto&scope=activity:write,read"
    }

    override suspend fun exchangeAuthorizationCode(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<StravaConfig> {
        return Result.success(
            StravaConfig(
                isConnected = true,
                accessToken = "ios_strava_access_token",
                refreshToken = "ios_strava_refresh_token",
                tokenExpiresAt = (kotlinx.datetime.Clock.System.now().toEpochMilliseconds() / 1000L) + 21600,
                athleteId = "1001",
                athleteName = "Atlet Strava",
                athleteUsername = "strava_ios_user",
                clientId = clientId,
                clientSecret = clientSecret,
                autoPostOnFinish = true,
            )
        )
    }

    override suspend fun refreshToken(config: StravaConfig): Result<StravaConfig> {
        return Result.success(config)
    }

    override suspend fun verifyToken(accessToken: String): Result<StravaAthlete> {
        return Result.success(
            StravaAthlete(
                id = 1001L,
                firstname = "Atlet",
                lastname = "Strava",
                username = "strava_user",
                profile = "",
            )
        )
    }

    override suspend fun postActivity(
        config: StravaConfig,
        session: FastingSession,
    ): Result<StravaActivityResult> {
        return Result.success(
            StravaActivityResult(
                id = 2001L,
                name = "Heula Fasting: ${(session.durationMs / 3600000L)} Jam (${session.planLabel})",
                elapsedTime = (session.durationMs / 1000L).toInt(),
                type = config.defaultActivityType.ifBlank { "Workout" },
                startDate = "",
                webUrl = "https://www.strava.com",
            )
        )
    }
}
