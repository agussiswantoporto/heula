package id.heula.domain.usecase

import id.heula.data.repository.SessionRepository
import id.heula.data.repository.SettingsRepository
import id.heula.data.repository.WeightRepository
import id.heula.domain.model.FastingSession
import id.heula.domain.model.UserAccount
import id.heula.domain.model.UserCloudData
import id.heula.domain.model.WeightRecord
import id.heula.domain.repository.CloudSyncService
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class SyncManager(
    private val sessionRepository: SessionRepository,
    private val weightRepository: WeightRepository,
    private val settingsRepository: SettingsRepository,
    private val cloudSyncService: CloudSyncService,
) {
    suspend fun sync(user: UserAccount): Result<Long> {
        return try {
            val now = Clock.System.now().toEpochMilliseconds()

            // 1. Download existing data from cloud
            val cloudDataResult = cloudSyncService.downloadUserData(user.uid)
            val cloudData = cloudDataResult.getOrNull()

            // 2. Fetch current local data
            val localSessions = sessionRepository.observeAll().first()
            val localWeights = weightRepository.observeAll().first()
            val localSettings = settingsRepository.observe().first()

            // 3. Smart Merge Sessions
            val mergedSessions = mergeSessions(localSessions, cloudData?.sessions ?: emptyList())
            sessionRepository.clearAll()
            for (session in mergedSessions.reversed()) {
                sessionRepository.add(session)
            }

            // 4. Smart Merge Weights
            val mergedWeights = mergeWeights(localWeights, cloudData?.weights ?: emptyList())
            weightRepository.clearAll()
            for (w in mergedWeights) {
                weightRepository.add(w)
            }

            // 5. Smart Merge Settings
            val mergedSettings = if (cloudData != null && cloudData.settings.onboarded && !localSettings.onboarded) {
                cloudData.settings
            } else {
                localSettings
            }
            settingsRepository.save(mergedSettings)

            // 6. Upload merged data with user info to Cloud
            val payload = UserCloudData(
                user = user,
                lastSyncedAt = now,
                sessions = mergedSessions,
                weights = mergedWeights,
                settings = mergedSettings,
            )
            val uploadRes = cloudSyncService.uploadUserData(user, payload)
            if (uploadRes.isSuccess) {
                Result.success(now)
            } else {
                Result.failure(uploadRes.exceptionOrNull() ?: Exception("Gagal mengunggah data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mergeSessions(local: List<FastingSession>, cloud: List<FastingSession>): List<FastingSession> {
        val map = mutableMapOf<Long, FastingSession>()
        for (s in cloud) {
            map[s.startMs] = s
        }
        for (localS in local) {
            val existing = map[localS.startMs]
            if (existing == null) {
                map[localS.startMs] = localS
            } else {
                val completed = existing.completed || localS.completed
                val endMs = maxOf(existing.endMs, localS.endMs)
                val mood = localS.mood ?: existing.mood
                val note = if (!localS.note.isNullOrBlank()) localS.note else existing.note
                map[localS.startMs] = existing.copy(
                    completed = completed,
                    endMs = endMs,
                    mood = mood,
                    note = note,
                )
            }
        }
        return map.values.sortedByDescending { it.startMs }.mapIndexed { idx, item ->
            item.copy(id = (idx + 1).toLong())
        }
    }

    private fun mergeWeights(local: List<WeightRecord>, cloud: List<WeightRecord>): List<WeightRecord> {
        val map = mutableMapOf<Long, WeightRecord>()
        for (w in cloud) {
            map[w.recordedAt] = w
        }
        for (w in local) {
            map[w.recordedAt] = w
        }
        return map.values.sortedBy { it.recordedAt }.mapIndexed { idx, item ->
            item.copy(id = (idx + 1).toLong())
        }
    }
}
