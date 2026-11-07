package id.heula.domain.repository

import id.heula.domain.model.UserAccount
import id.heula.domain.model.UserCloudData

interface CloudSyncService {
    suspend fun uploadUserData(user: UserAccount, data: UserCloudData): Result<Unit>
    suspend fun downloadUserData(uid: String): Result<UserCloudData?>
}
