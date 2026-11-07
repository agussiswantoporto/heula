package id.heula.domain.repository

import id.heula.domain.model.UserCloudData

interface CloudSyncService {
    suspend fun uploadUserData(uid: String, data: UserCloudData): Result<Unit>
    suspend fun downloadUserData(uid: String): Result<UserCloudData?>
}
