package id.heula.strava

import id.heula.domain.model.FastingSession
import id.heula.domain.model.StravaAthlete
import id.heula.domain.model.StravaActivityResult
import id.heula.domain.model.StravaConfig
import id.heula.domain.repository.StravaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class AndroidStravaService : StravaService {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getAuthorizationUrl(clientId: String, redirectUri: String): String {
        val encodedRedirect = URLEncoder.encode(redirectUri, "UTF-8")
        return "https://www.strava.com/oauth/authorize?client_id=$clientId&response_type=code&redirect_uri=$encodedRedirect&approval_prompt=auto&scope=activity:write,read"
    }

    override suspend fun exchangeAuthorizationCode(
        clientId: String,
        clientSecret: String,
        code: String,
    ): Result<StravaConfig> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://www.strava.com/oauth/token")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 15000
            }

            val params = "client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") +
                "&code=" + URLEncoder.encode(code, "UTF-8") +
                "&grant_type=authorization_code"

            conn.outputStream.use { it.write(params.toByteArray(Charsets.UTF_8)) }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val body = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val root = json.parseToJsonElement(body).jsonObject

                val accessToken = root["access_token"]?.jsonPrimitive?.content ?: ""
                val refreshToken = root["refresh_token"]?.jsonPrimitive?.content ?: ""
                val expiresAt = root["expires_at"]?.jsonPrimitive?.longOrNull ?: 0L

                val athleteObj = root["athlete"]?.jsonObject
                val athleteId = athleteObj?.get("id")?.jsonPrimitive?.content ?: ""
                val firstname = athleteObj?.get("firstname")?.jsonPrimitive?.content ?: ""
                val lastname = athleteObj?.get("lastname")?.jsonPrimitive?.content ?: ""
                val username = athleteObj?.get("username")?.jsonPrimitive?.content ?: ""
                val profile = athleteObj?.get("profile")?.jsonPrimitive?.content ?: ""
                val fullName = when {
                    firstname.isNotBlank() && lastname.isNotBlank() -> "$firstname $lastname"
                    firstname.isNotBlank() -> firstname
                    username.isNotBlank() -> username
                    else -> "Atlet Strava"
                }

                Result.success(
                    StravaConfig(
                        isConnected = true,
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        tokenExpiresAt = expiresAt,
                        athleteId = athleteId,
                        athleteName = fullName,
                        athleteUsername = username,
                        athleteProfileUrl = profile,
                        clientId = clientId,
                        clientSecret = clientSecret,
                        autoPostOnFinish = true,
                    )
                )
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(Exception("Gagal menukar kode login Strava: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(config: StravaConfig): Result<StravaConfig> = withContext(Dispatchers.IO) {
        try {
            if (config.refreshToken.isBlank() || config.clientId.isBlank() || config.clientSecret.isBlank()) {
                return@withContext Result.failure(Exception("Data refresh token Strava tidak lengkap"))
            }

            val url = URL("https://www.strava.com/oauth/token")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 15000
            }

            val params = "client_id=" + URLEncoder.encode(config.clientId, "UTF-8") +
                "&client_secret=" + URLEncoder.encode(config.clientSecret, "UTF-8") +
                "&refresh_token=" + URLEncoder.encode(config.refreshToken, "UTF-8") +
                "&grant_type=refresh_token"

            conn.outputStream.use { it.write(params.toByteArray(Charsets.UTF_8)) }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val body = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val root = json.parseToJsonElement(body).jsonObject

                val newAccessToken = root["access_token"]?.jsonPrimitive?.content ?: config.accessToken
                val newRefreshToken = root["refresh_token"]?.jsonPrimitive?.content ?: config.refreshToken
                val newExpiresAt = root["expires_at"]?.jsonPrimitive?.longOrNull ?: config.tokenExpiresAt

                Result.success(
                    config.copy(
                        accessToken = newAccessToken,
                        refreshToken = newRefreshToken,
                        tokenExpiresAt = newExpiresAt,
                    )
                )
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(Exception("Gagal memperbarui token Strava: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyToken(accessToken: String): Result<StravaAthlete> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://www.strava.com/api/v3/athlete")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Bearer $accessToken")
                connectTimeout = 10000
                readTimeout = 10000
            }

            val code = conn.responseCode
            if (code in 200..299) {
                val body = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val root = json.parseToJsonElement(body).jsonObject
                val id = root["id"]?.jsonPrimitive?.longOrNull ?: 0L
                val firstname = root["firstname"]?.jsonPrimitive?.content ?: ""
                val lastname = root["lastname"]?.jsonPrimitive?.content ?: ""
                val username = root["username"]?.jsonPrimitive?.content ?: ""
                val profile = root["profile"]?.jsonPrimitive?.content ?: ""

                Result.success(
                    StravaAthlete(
                        id = id,
                        firstname = firstname,
                        lastname = lastname,
                        username = username,
                        profile = profile,
                    )
                )
            } else if (code == 401) {
                Result.failure(Exception("Token Strava tidak valid atau kedaluwarsa (401 Unauthorized)."))
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $code"
                Result.failure(Exception("Gagal menghubungi Strava: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun postActivity(
        config: StravaConfig,
        session: FastingSession,
    ): Result<StravaActivityResult> = withContext(Dispatchers.IO) {
        try {
            // Check if token needs refresh
            val effectiveConfig = if (config.tokenExpiresAt > 0 &&
                (System.currentTimeMillis() / 1000L >= config.tokenExpiresAt - 300) &&
                config.refreshToken.isNotBlank()
            ) {
                refreshToken(config).getOrDefault(config)
            } else {
                config
            }

            val token = effectiveConfig.accessToken.trim()
            if (token.isBlank()) {
                return@withContext Result.failure(Exception("Access token Strava belum diatur"))
            }

            val durHours = session.durationMs / 3600000L
            val durMins = (session.durationMs % 3600000L) / 60000L
            val elapsedSec = maxOf(60, (session.durationMs / 1000L).toInt())
            val status = if (session.completed) "Target Tuntas! 🎉" else "Disudahi Lebih Awal"
            val moodText = session.mood?.let { "\n⚡ Mood: ${it.name}" } ?: ""
            val noteText = if (!session.note.isNullOrBlank()) "\n📝 Catatan Buka Puasa: ${session.note}" else ""

            val title = "Heula Fasting: $durHours Jam $durMins Mnt (${session.planLabel})"
            val description = """
                Puasa Berkala bersama Heula 🌿
                ⏱️ Durasi: $durHours jam $durMins menit
                🎯 Target: ${session.targetHours.toInt()} jam ($status)$moodText$noteText

                Pelacak Puasa Berkala: heula.website
            """.trimIndent()

            val isoStart = Instant.fromEpochMilliseconds(session.startMs).toString()
            val activityType = effectiveConfig.defaultActivityType.ifBlank { "Workout" }

            val url = URL("https://www.strava.com/api/v3/activities")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $token")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 15000
            }

            val payload = """{
                "name": ${json.encodeToString(kotlinx.serialization.serializer(), title)},
                "type": ${json.encodeToString(kotlinx.serialization.serializer(), activityType)},
                "sport_type": ${json.encodeToString(kotlinx.serialization.serializer(), activityType)},
                "start_date_local": "$isoStart",
                "elapsed_time": $elapsedSec,
                "description": ${json.encodeToString(kotlinx.serialization.serializer(), description)},
                "trainer": 0,
                "commute": 0
            }""".trimIndent()

            conn.outputStream.use { it.write(payload.toByteArray(Charsets.UTF_8)) }

            val code = conn.responseCode
            if (code in 200..299) {
                val body = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                val root = json.parseToJsonElement(body).jsonObject
                val id = root["id"]?.jsonPrimitive?.longOrNull ?: 0L
                val name = root["name"]?.jsonPrimitive?.content ?: title
                val webUrl = "https://www.strava.com/activities/$id"

                Result.success(
                    StravaActivityResult(
                        id = id,
                        name = name,
                        elapsedTime = elapsedSec,
                        type = activityType,
                        startDate = isoStart,
                        webUrl = webUrl,
                    )
                )
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $code"
                Result.failure(Exception("Gagal posting ke Strava: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
