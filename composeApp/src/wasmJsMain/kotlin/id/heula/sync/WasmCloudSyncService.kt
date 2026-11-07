package id.heula.sync

import id.heula.domain.model.UserAccount
import id.heula.domain.model.UserCloudData
import id.heula.domain.repository.CloudSyncService
import kotlin.JsFun
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@JsFun("(uid, email, name, photo, json) => { window.localStorage.removeItem('heula_sync_upload_status'); if (window.heulaFirestore) { window.heulaFirestore.upload(uid, email, name, photo, json); } }")
external fun jsUploadFirestore(uid: String, email: String, name: String, photo: String, json: String)

@JsFun("(uid) => { window.localStorage.removeItem('heula_sync_download_result'); if (window.heulaFirestore) { window.heulaFirestore.download(uid); } }")
external fun jsDownloadFirestore(uid: String)

@JsFun("() => { return window.localStorage.getItem('heula_sync_upload_status') || ''; }")
external fun getJsUploadStatus(): String

@JsFun("() => { return window.localStorage.getItem('heula_sync_download_result') || ''; }")
external fun getJsDownloadResult(): String

class WasmCloudSyncService : CloudSyncService {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun uploadUserData(user: UserAccount, data: UserCloudData): Result<Unit> {
        val serialized = json.encodeToString(data)
        jsUploadFirestore(
            uid = user.uid,
            email = user.email,
            name = user.displayName ?: "",
            photo = user.photoUrl ?: "",
            json = serialized,
        )

        val status = withTimeoutOrNull(15_000L) {
            while (true) {
                val res = getJsUploadStatus()
                if (res.isNotEmpty()) return@withTimeoutOrNull res
                delay(200)
            }
            null
        }

        return when {
            status == "OK" -> Result.success(Unit)
            status?.startsWith("ERR:") == true -> Result.failure(Exception(status.removePrefix("ERR:")))
            else -> Result.failure(Exception("Waktu upload ke cloud habis"))
        }
    }

    override suspend fun downloadUserData(uid: String): Result<UserCloudData?> {
        jsDownloadFirestore(uid)

        val result = withTimeoutOrNull(15_000L) {
            while (true) {
                val res = getJsDownloadResult()
                if (res.isNotEmpty()) return@withTimeoutOrNull res
                delay(200)
            }
            null
        }

        return when {
            result == "__EMPTY__" || result == null -> Result.success(null)
            result.startsWith("__ERR__:") -> Result.failure(Exception(result.removePrefix("__ERR__:")))
            else -> try {
                Result.success(json.decodeFromString<UserCloudData>(result))
            } catch (e: Exception) {
                Result.failure(Exception("Gagal membaca data dari cloud: ${e.message}"))
            }
        }
    }
}
