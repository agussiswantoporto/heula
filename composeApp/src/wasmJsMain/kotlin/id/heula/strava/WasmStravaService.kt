package id.heula.strava

import id.heula.domain.model.FastingSession
import id.heula.domain.model.StravaAthlete
import id.heula.domain.model.StravaActivityResult
import id.heula.domain.model.StravaConfig
import id.heula.domain.repository.StravaService
import kotlin.JsFun
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

@JsFun("(token) => { if (window.heulaStrava) { window.heulaStrava.verifyToken(token); } }")
external fun jsStravaVerify(token: String)

@JsFun("() => { return window.localStorage.getItem('heula_strava_verify_result') || ''; }")
external fun getJsStravaVerifyResult(): String

@JsFun("(token, jsonPayload) => { if (window.heulaStrava) { window.heulaStrava.postActivity(token, jsonPayload); } }")
external fun jsStravaPostActivity(token: String, jsonPayload: String)

@JsFun("() => { return window.localStorage.getItem('heula_strava_post_result') || ''; }")
external fun getJsStravaPostResult(): String

class WasmStravaService : StravaService {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun verifyToken(accessToken: String): Result<StravaAthlete> {
        val cleanToken = accessToken.trim()
        if (cleanToken.isBlank()) {
            return Result.failure(Exception("Access token Strava belum diatur"))
        }

        jsStravaVerify(cleanToken)

        val raw = withTimeoutOrNull(12_000L) {
            while (true) {
                val res = getJsStravaVerifyResult()
                if (res.isNotEmpty()) return@withTimeoutOrNull res
                delay(150)
            }
            null
        } ?: return Result.failure(Exception("Waktu verifikasi Strava habis"))

        return when {
            raw.startsWith("OK:") -> {
                try {
                    val athleteJson = raw.removePrefix("OK:")
                    val root = json.parseToJsonElement(athleteJson).jsonObject
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
                } catch (e: Exception) {
                    Result.failure(Exception("Gagal memproses data atlet Strava: ${e.message}"))
                }
            }
            raw.startsWith("ERR:") -> Result.failure(Exception(raw.removePrefix("ERR:")))
            else -> Result.failure(Exception("Respon verifikasi tidak dikenal"))
        }
    }

    override suspend fun postActivity(
        config: StravaConfig,
        session: FastingSession,
    ): Result<StravaActivityResult> {
        val token = config.accessToken.trim()
        if (token.isBlank()) {
            return Result.failure(Exception("Access token Strava belum diatur"))
        }

        val durHours = session.durationMs / 3600000L
        val durMins = (session.durationMs % 3600000L) / 60000L
        val elapsedSec = maxOf(60, (session.durationMs / 1000L).toInt())
        val status = if (session.completed) "Target Tuntas! 🎉" else "Disudahi Lebih Awal"
        val moodText = session.mood?.let { "\n⚡ Mood: ${it.name}" } ?: ""
        val noteText = if (!session.note.isNullOrBlank()) "\n📝 Catatan: ${session.note}" else ""

        val title = "Heula Fasting: $durHours Jam $durMins Mnt (${session.planLabel})"
        val description = """
            Puasa Berkala bersama Heula 🌿
            ⏱️ Durasi: $durHours jam $durMins menit
            🎯 Target: ${session.targetHours.toInt()} jam ($status)$moodText$noteText

            Pelacak Puasa Berkala: heula.website
        """.trimIndent()

        val isoStart = Instant.fromEpochMilliseconds(session.startMs).toString()
        val activityType = config.defaultActivityType.ifBlank { "Workout" }

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

        jsStravaPostActivity(token, payload)

        val raw = withTimeoutOrNull(15_000L) {
            while (true) {
                val res = getJsStravaPostResult()
                if (res.isNotEmpty()) return@withTimeoutOrNull res
                delay(150)
            }
            null
        } ?: return Result.failure(Exception("Waktu pengunggahan ke Strava habis"))

        return when {
            raw.startsWith("OK:") -> {
                try {
                    val resultJson = raw.removePrefix("OK:")
                    val root = json.parseToJsonElement(resultJson).jsonObject
                    val id = root["id"]?.jsonPrimitive?.longOrNull ?: 0L
                    val name = root["name"]?.jsonPrimitive?.content ?: title
                    val webUrl = root["webUrl"]?.jsonPrimitive?.content ?: "https://www.strava.com/activities/$id"
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
                } catch (e: Exception) {
                    Result.failure(Exception("Gagal membaca hasil posting Strava: ${e.message}"))
                }
            }
            raw.startsWith("ERR:") -> Result.failure(Exception(raw.removePrefix("ERR:")))
            else -> Result.failure(Exception("Gagal mengunggah ke Strava"))
        }
    }
}
