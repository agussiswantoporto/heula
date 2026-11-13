package id.heula.strava

import id.heula.domain.model.FastingSession
import id.heula.domain.model.StravaAthlete
import id.heula.domain.model.StravaActivityResult
import id.heula.domain.model.StravaConfig
import id.heula.domain.repository.StravaService

class IosStravaService : StravaService {
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
