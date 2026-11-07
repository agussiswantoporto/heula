package id.heula.sync

import id.heula.domain.model.UserAccount
import id.heula.domain.model.UserCloudData
import id.heula.domain.repository.CloudSyncService

class IosCloudSyncService : CloudSyncService {
    override suspend fun uploadUserData(user: UserAccount, data: UserCloudData): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun downloadUserData(uid: String): Result<UserCloudData?> {
        return Result.success(null)
    }
}
