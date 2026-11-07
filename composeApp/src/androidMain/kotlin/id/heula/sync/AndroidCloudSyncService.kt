package id.heula.sync

import id.heula.domain.model.UserCloudData
import id.heula.domain.repository.CloudSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL

class AndroidCloudSyncService : CloudSyncService {
    private val projectId = "heula-c9a5f"
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun uploadUserData(uid: String, data: UserCloudData): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/users/$uid?updateMask.fieldPaths=payload&updateMask.fieldPaths=updatedAt")
            val serialized = json.encodeToString(data)
            val body = """{"fields":{"payload":{"stringValue":${json.encodeToString(serialized)}},"updatedAt":{"integerValue":"${System.currentTimeMillis()}"}}}"""

            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("X-HTTP-Method-Override", "PATCH")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 10000
            }
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            if (code in 200..299) {
                Result.success(Unit)
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $code"
                Result.failure(Exception("Gagal upload: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadUserData(uid: String): Result<UserCloudData?> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/users/$uid")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
            }
            val code = conn.responseCode
            if (code == 404) return@withContext Result.success(null)
            if (code in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val root = json.parseToJsonElement(responseText).jsonObject
                val fields = root["fields"]?.jsonObject
                val payloadString = fields?.get("payload")?.jsonObject?.get("stringValue")?.jsonPrimitive?.content
                if (payloadString.isNullOrEmpty()) {
                    Result.success(null)
                } else {
                    Result.success(json.decodeFromString<UserCloudData>(payloadString))
                }
            } else {
                Result.failure(Exception("Gagal download: HTTP $code"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
