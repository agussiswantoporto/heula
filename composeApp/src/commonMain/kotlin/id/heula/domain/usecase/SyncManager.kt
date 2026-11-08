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

            // 3. Collect deleted tombstones from local and cloud
            val allDeletedSessions = (sessionRepository.getDeletedStartMs() + (cloudData?.deletedSessions ?: emptyList())).toSet()
            val allDeletedWeights = (weightRepository.getDeletedTimestamps() + (cloudData?.deletedWeights ?: emptyList())).toSet()

            sessionRepository.markDeleted(allDeletedSessions)
            weightRepository.markDeleted(allDeletedWeights)

            // 4. Smart Merge Sessions (excluding deleted)
            val mergedSessions = mergeSessions(localSessions, cloudData?.sessions ?: emptyList(), allDeletedSessions)
            sessionRepository.replaceAll(mergedSessions)

            // 5. Smart Merge Weights (excluding deleted)
            val mergedWeights = mergeWeights(localWeights, cloudData?.weights ?: emptyList(), allDeletedWeights)
            weightRepository.replaceAll(mergedWeights)

            // 6. Smart Merge Settings (Includes active fasting counter & eating window)
            val mergedSettings = mergeSettings(localSettings, cloudData?.settings, mergedSessions)
            settingsRepository.save(mergedSettings)

            // 7. Upload merged data with user info & tombstones to Cloud
            val payload = UserCloudData(
                user = user,
                lastSyncedAt = now,
                sessions = mergedSessions,
                weights = mergedWeights,
                settings = mergedSettings,
                deletedSessions = allDeletedSessions.toList(),
                deletedWeights = allDeletedWeights.toList(),
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

    private fun mergeSessions(
        local: List<FastingSession>,
        cloud: List<FastingSession>,
        deletedStartMs: Set<Long>,
    ): List<FastingSession> {
        val map = mutableMapOf<Long, FastingSession>()
        for (s in cloud) {
            if (s.startMs !in deletedStartMs) {
                map[s.startMs] = s
            }
        }
        for (localS in local) {
            if (localS.startMs !in deletedStartMs) {
                val existing = map[localS.startMs]
                if (existing == null) {
                    map[localS.startMs] = localS
                } else {
                    val completed = existing.completed || localS.completed
                    val endMs = if (localS.endMs > 0L) localS.endMs else existing.endMs
                    val mood = localS.mood ?: existing.mood
                    val note = if (!localS.note.isNullOrBlank()) localS.note else existing.note
                    map[localS.startMs] = localS.copy(
                        completed = completed,
                        endMs = endMs,
                        mood = mood,
                        note = note,
                    )
                }
            }
        }
        return map.values.sortedByDescending { it.startMs }.mapIndexed { idx, item ->
            item.copy(id = (idx + 1).toLong())
        }
    }

    private fun mergeWeights(
        local: List<WeightRecord>,
        cloud: List<WeightRecord>,
        deletedTimestamps: Set<Long>,
    ): List<WeightRecord> {
        val map = mutableMapOf<Long, WeightRecord>()
        for (w in cloud) {
            if (w.recordedAt !in deletedTimestamps) {
                map[w.recordedAt] = w
            }
        }
        for (w in local) {
            if (w.recordedAt !in deletedTimestamps) {
                map[w.recordedAt] = w
            }
        }
        return map.values.sortedBy { it.recordedAt }.mapIndexed { idx, item ->
            item.copy(id = (idx + 1).toLong())
        }
    }

    private fun mergeSettings(
        local: AppSettings,
        cloud: AppSettings?,
        sessions: List<FastingSession>,
    ): AppSettings {
        if (cloud == null) return local

        val onboarded = local.onboarded || cloud.onboarded
        val profiling = local.profiling ?: cloud.profiling
        val chosenMethod = if (local.chosenMethod != id.heula.domain.model.MethodId.SIXTEEN_EIGHT) local.chosenMethod else cloud.chosenMethod
        val customFastingHours = if (local.customFastingHours != 16.0) local.customFastingHours else cloud.customFastingHours
        val dailyGoalHours = if (local.dailyGoalHours != 16.0) local.dailyGoalHours else cloud.dailyGoalHours

        // Check if an active fast has already been recorded in completed sessions
        val localFast = local.activeFast?.takeIf { fast ->
            sessions.none { it.startMs == fast.startMs }
        }
        val cloudFast = cloud.activeFast?.takeIf { fast ->
            sessions.none { it.startMs == fast.startMs }
        }

        // Determine resolved active fast and eating window state
        val (resolvedActiveFast, resolvedEatingStartMs, resolvedEatingHours) = when {
            // Case 1: Both devices report an active fast -> most recently modified wins
            localFast != null && cloudFast != null -> {
                if (localFast.updatedAtMs >= cloudFast.updatedAtMs) {
                    Triple(localFast, null, local.eatingWindowHours)
                } else {
                    Triple(cloudFast, null, cloud.eatingWindowHours)
                }
            }
            // Case 2: Only local has active fast
            localFast != null -> {
                val cloudEatingStart = cloud.eatingWindowStartMs ?: 0L
                if (cloudEatingStart > localFast.updatedAtMs) {
                    Triple(null, cloudEatingStart, cloud.eatingWindowHours)
                } else {
                    Triple(localFast, null, local.eatingWindowHours)
                }
            }
            // Case 3: Only cloud has active fast
            cloudFast != null -> {
                val localEatingStart = local.eatingWindowStartMs ?: 0L
                if (localEatingStart > cloudFast.updatedAtMs) {
                    Triple(null, localEatingStart, local.eatingWindowHours)
                } else {
                    Triple(cloudFast, null, cloud.eatingWindowHours)
                }
            }
            // Case 4: Neither is actively fasting -> sync eating window
            else -> {
                val localEating = local.eatingWindowStartMs ?: 0L
                val cloudEating = cloud.eatingWindowStartMs ?: 0L
                val latestEating = maxOf(localEating, cloudEating)
                val hours = if (latestEating == localEating && localEating > 0L) local.eatingWindowHours else cloud.eatingWindowHours
                Triple(null, if (latestEating > 0L) latestEating else null, hours)
            }
        }

        val todayEpochDay = Clock.System.now().toEpochMilliseconds() / 86_400_000L
        val localWaterToday = if (local.waterDateEpochDay == todayEpochDay) local.waterIntakeMl else 0
        val cloudWaterToday = if (cloud.waterDateEpochDay == todayEpochDay) cloud.waterIntakeMl else 0
        val mergedWater = maxOf(localWaterToday, cloudWaterToday)

        return local.copy(
            onboarded = onboarded,
            profiling = profiling,
            chosenMethod = chosenMethod,
            customFastingHours = customFastingHours,
            dailyGoalHours = dailyGoalHours,
            activeFast = resolvedActiveFast,
            eatingWindowStartMs = resolvedEatingStartMs,
            eatingWindowHours = resolvedEatingHours,
            waterIntakeMl = mergedWater,
            waterDateEpochDay = todayEpochDay,
        )
    }
}
