package id.heula.domain.repository

import id.heula.domain.model.FastingSession
import id.heula.domain.model.StravaAthlete
import id.heula.domain.model.StravaActivityResult
import id.heula.domain.model.StravaConfig

interface StravaService {
    suspend fun verifyToken(accessToken: String): Result<StravaAthlete>
    suspend fun postActivity(
        config: StravaConfig,
        session: FastingSession,
    ): Result<StravaActivityResult>
}
