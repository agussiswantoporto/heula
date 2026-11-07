package id.heula.domain.repository

import androidx.compose.ui.graphics.ImageBitmap
import id.heula.domain.model.FastingSession
import id.heula.domain.model.ShareCustomization

interface ShareExporter {
    suspend fun exportFastingShare(session: FastingSession, config: ShareCustomization): Result<Unit>
    suspend fun generatePreviewBitmap(session: FastingSession, config: ShareCustomization): ImageBitmap? = null
}
