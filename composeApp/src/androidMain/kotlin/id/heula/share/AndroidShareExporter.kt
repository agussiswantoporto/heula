package id.heula.share

import androidx.compose.ui.graphics.ImageBitmap
import id.heula.domain.model.FastingSession
import id.heula.domain.model.ShareCustomization
import id.heula.domain.repository.ShareExporter

class AndroidShareExporter : ShareExporter {
    override suspend fun exportFastingShare(session: FastingSession, config: ShareCustomization): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun generatePreviewBitmap(session: FastingSession, config: ShareCustomization): ImageBitmap? {
        return null
    }
}
