package id.heula.share

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import id.heula.domain.model.FastingSession
import id.heula.domain.model.ShareCustomization
import id.heula.domain.repository.ShareExporter
import kotlin.JsFun
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.skia.Image

@JsFun("(sessionJson, configJson) => { window.localStorage.removeItem('heula_share_export_status'); if (window.heulaShareImage) { window.heulaShareImage.exportImage(sessionJson, configJson); } }")
external fun jsExportShareImage(sessionJson: String, configJson: String)

@JsFun("() => { return window.localStorage.getItem('heula_share_export_status') || ''; }")
external fun getJsShareExportStatus(): String

@JsFun("(sessionJson, configJson) => { if (window.heulaShareImage && window.heulaShareImage.renderBase64Png) { return window.heulaShareImage.renderBase64Png(sessionJson, configJson); } return ''; }")
external fun jsRenderShareImageBase64(sessionJson: String, configJson: String): String

class WasmShareExporter : ShareExporter {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun exportFastingShare(session: FastingSession, config: ShareCustomization): Result<Unit> {
        val sessionJson = json.encodeToString(session)
        val configJson = json.encodeToString(config)

        jsExportShareImage(sessionJson, configJson)

        val status = withTimeoutOrNull(20_000L) {
            while (true) {
                val s = getJsShareExportStatus()
                if (s.isNotEmpty()) return@withTimeoutOrNull s
                delay(200)
            }
            null
        }

        return when {
            status == "OK" -> Result.success(Unit)
            status?.startsWith("ERR:") == true -> Result.failure(Exception(status.removePrefix("ERR:")))
            else -> Result.failure(Exception("Waktu ekspor gambar habis"))
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun generatePreviewBitmap(session: FastingSession, config: ShareCustomization): ImageBitmap? {
        return try {
            val sessionJson = json.encodeToString(session)
            val configJson = json.encodeToString(config)
            val base64 = jsRenderShareImageBase64(sessionJson, configJson)
            if (base64.isNotEmpty()) {
                val bytes = Base64.decode(base64)
                Image.makeFromEncoded(bytes).toComposeImageBitmap()
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
