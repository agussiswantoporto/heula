package id.heula.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StravaConfig(
    val isConnected: Boolean = false,
    val accessToken: String = "",
    val refreshToken: String = "",
    val tokenExpiresAt: Long = 0L,
    val athleteId: String = "",
    val athleteName: String = "",
    val athleteUsername: String = "",
    val athleteProfileUrl: String = "",
    val clientId: String = "",
    val clientSecret: String = "",
    val autoPostOnFinish: Boolean = true,
    val defaultActivityType: String = "Workout",
    val lastPostedAt: Long? = null,
)

@Serializable
data class StravaAthlete(
    val id: Long = 0L,
    val firstname: String = "",
    val lastname: String = "",
    val username: String = "",
    val profile: String = "",
) {
    val fullName: String
        get() = when {
            firstname.isNotBlank() && lastname.isNotBlank() -> "$firstname $lastname"
            firstname.isNotBlank() -> firstname
            username.isNotBlank() -> username
            else -> "Atlet Strava"
        }
}

@Serializable
data class StravaActivityResult(
    val id: Long = 0L,
    val name: String = "",
    val elapsedTime: Int = 0,
    val type: String = "Workout",
    val startDate: String = "",
    val webUrl: String? = null,
)
