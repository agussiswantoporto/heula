package id.heula.share

import id.heula.domain.model.FastingSession
import id.heula.domain.model.ShareCustomization
import id.heula.domain.repository.ShareExporter

class IosShareExporter : ShareExporter {
    override suspend fun exportFastingShare(session: FastingSession, config: ShareCustomization): Result<Unit> {
        return Result.success(Unit)
    }
}
