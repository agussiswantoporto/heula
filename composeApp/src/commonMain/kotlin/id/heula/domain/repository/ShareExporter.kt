package id.heula.domain.repository

import id.heula.domain.model.FastingSession
import id.heula.domain.model.ShareCustomization

interface ShareExporter {
    suspend fun exportFastingShare(session: FastingSession, config: ShareCustomization): Result<Unit>
}
