package id.heula.presentation.store

import id.heula.data.repository.SessionRepository
import id.heula.data.repository.SettingsRepository
import id.heula.data.repository.WeightRepository
import id.heula.domain.model.*
import id.heula.domain.repository.AuthRepository
import id.heula.domain.repository.HealthSyncService
import id.heula.domain.repository.ShareExporter
import id.heula.domain.repository.StravaService
import id.heula.domain.usecase.AppSettings
import id.heula.domain.usecase.toActiveFast
import id.heula.domain.usecase.toSerializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class AppStore(
    private val settingsRepository: SettingsRepository,
    private val sessionRepository: SessionRepository,
    private val weightRepository: WeightRepository,
    private val authRepository: AuthRepository,
    private val healthSyncService: HealthSyncService,
    private val shareExporter: ShareExporter,
    private val stravaService: StravaService,
    private val scope: CoroutineScope,
) {
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    val currentUser: StateFlow<UserAccount?> = authRepository.currentUser
    val syncState: StateFlow<SyncState> = authRepository.syncState
    val lastSyncedAt: StateFlow<Long?> = authRepository.lastSyncedAt
    val healthStatus: StateFlow<HealthSyncStatus> = healthSyncService.status
    val stravaConfig: StateFlow<StravaConfig> = settings
        .map { it.stravaConfig }
        .stateIn(scope, SharingStarted.Eagerly, StravaConfig())

    val activeFast: StateFlow<ActiveFast?> = settings
        .map { it.activeFast?.toActiveFast() }
        .stateIn(scope, SharingStarted.Eagerly, null)

    val sessions = sessionRepository.observeAll()
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val weights = weightRepository.observeAll()
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    fun signInWithGoogle(onResult: (Result<UserAccount>) -> Unit = {}) {
        scope.launch {
            val res = authRepository.signInWithGoogle()
            onResult(res)
        }
    }

    fun signOut(onResult: (Result<Unit>) -> Unit = {}) {
        scope.launch {
            val res = authRepository.signOut()
            onResult(res)
        }
    }

    fun syncAllData(onResult: (Result<Unit>) -> Unit = {}) {
        scope.launch {
            val res = authRepository.syncAllData()
            onResult(res)
        }
    }

    fun requestHealthAuthorization(onResult: (Result<Boolean>) -> Unit = {}) {
        scope.launch {
            val res = healthSyncService.requestAuthorization()
            onResult(res)
        }
    }

    fun syncHealthData(onResult: (Result<Unit>) -> Unit = {}) {
        scope.launch {
            val currentWeights = weights.value
            val currentWater = settings.value.waterIntakeMl
            val res = healthSyncService.syncAll(currentWeights, currentWater)
            onResult(res)
        }
    }

    fun exportShareImage(
        session: FastingSession,
        config: ShareCustomization,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val res = shareExporter.exportFastingShare(session, config)
            onResult(res)
        }
    }

    suspend fun generateSharePreview(session: FastingSession, config: ShareCustomization): androidx.compose.ui.graphics.ImageBitmap? {
        return shareExporter.generatePreviewBitmap(session, config)
    }

    fun updateStravaConfig(config: StravaConfig) {
        update { copy(stravaConfig = config) }
    }

    fun disconnectStrava() {
        update { copy(stravaConfig = StravaConfig(isConnected = false)) }
    }

    fun setStravaAutoPost(enabled: Boolean) {
        update { copy(stravaConfig = stravaConfig.copy(autoPostOnFinish = enabled)) }
    }

    fun verifyAndConnectStrava(token: String, onResult: (Result<StravaAthlete>) -> Unit = {}) {
        scope.launch {
            val cleanToken = token.trim()
            if (cleanToken.isBlank()) {
                onResult(Result.failure(IllegalArgumentException("Access token Strava tidak boleh kosong")))
                return@launch
            }
            val res = stravaService.verifyToken(cleanToken)
            res.onSuccess { athlete ->
                val newConfig = _settings.value.stravaConfig.copy(
                    isConnected = true,
                    accessToken = cleanToken,
                    athleteId = athlete.id.toString(),
                    athleteName = athlete.fullName,
                    athleteUsername = athlete.username,
                    athleteProfileUrl = athlete.profile,
                )
                update { copy(stravaConfig = newConfig) }
            }
            onResult(res)
        }
    }

    fun postSessionToStrava(
        session: FastingSession,
        onResult: (Result<StravaActivityResult>) -> Unit = {}
    ) {
        scope.launch {
            val config = _settings.value.stravaConfig
            if (!config.isConnected || config.accessToken.isBlank()) {
                onResult(Result.failure(IllegalStateException("Strava belum terhubung")))
                return@launch
            }
            val res = stravaService.postActivity(config, session)
            res.onSuccess {
                val now = Clock.System.now().toEpochMilliseconds()
                update { copy(stravaConfig = stravaConfig.copy(lastPostedAt = now)) }
            }
            onResult(res)
        }
    }

    fun triggerAutoSync() {
        if (currentUser.value != null) {
            scope.launch {
                authRepository.syncAllData()
            }
        }
    }

    init {
        scope.launch {
            settingsRepository.observe().collect { _settings.value = it }
        }
        scope.launch {
            healthSyncService.checkStatus()
        }
        // Auto-sync immediately on launch if user is logged in
        scope.launch {
            delay(1200L) // Wait for initial local settings to load
            if (currentUser.value != null && syncState.value != SyncState.SYNCING) {
                authRepository.syncAllData()
            }
        }
        // Background cross-device sync loop: polls every 25 seconds for counter & session updates
        scope.launch {
            while (isActive) {
                delay(25_000L)
                if (currentUser.value != null && syncState.value != SyncState.SYNCING) {
                    authRepository.syncAllData()
                }
            }
        }
    }

    private fun update(block: AppSettings.() -> AppSettings) {
        scope.launch {
            val updated = _settings.value.block()
            _settings.value = updated
            settingsRepository.save(updated)
        }
    }

    fun selectTab(tab: ScreenTab) = update {
        copy(lastTab = tab)
    }

    fun completeOnboarding(answers: ProfilingAnswers, method: MethodId) {
        logWeight(answers.weightKg)
        update {
            copy(
                onboarded = true,
                profiling = answers,
                chosenMethod = method,
                dailyGoalHours = resolveTargetHours(method, customFastingHours),
            )
        }
    }

    fun setChosenMethod(id: MethodId) = update {
        copy(
            chosenMethod = id,
            dailyGoalHours = resolveTargetHours(id, customFastingHours),
        )
    }

    fun setMethod(id: MethodId) = setChosenMethod(id)

    fun setCustomFastingHours(h: Double) = update {
        val clamped = h.coerceIn(1.0, 23.0)
        copy(
            customFastingHours = clamped,
            dailyGoalHours = if (chosenMethod == MethodId.CUSTOM) clamped else dailyGoalHours,
        )
    }

    fun setTheme(theme: Theme) = update { copy(theme = theme) }

    fun setLanguage(language: AppLanguage) = update { copy(language = language) }

    fun setReminders(endReminder: Boolean, startReminder: Boolean = false) = update {
        copy(reminders = Reminders(endReminder = endReminder, startReminder = startReminder))
    }

    fun setReminders(reminders: Reminders) = update { copy(reminders = reminders) }

    fun startFast(nowMs: Long = Clock.System.now().toEpochMilliseconds()) {
        update {
            copy(
                activeFast = ActiveFast(
                    startMs = nowMs,
                    targetHours = resolveTargetHours(chosenMethod, customFastingHours),
                    planId = chosenMethod,
                    planLabel = resolvePlanLabel(chosenMethod, customFastingHours),
                    updatedAtMs = nowMs,
                ).toSerializable(),
                eatingWindowStartMs = null, // Close eating window when fasting starts
            )
        }
        triggerAutoSync()
    }

    fun editStartTime(startMs: Long) {
        val now = Clock.System.now().toEpochMilliseconds()
        update {
            val fast = activeFast ?: return@update this
            if (startMs > now + 60_000L) return@update this
            val safeStartMs = startMs.coerceAtMost(now)
            copy(activeFast = fast.copy(startMs = safeStartMs, updatedAtMs = now))
        }
        triggerAutoSync()
    }

    fun updateStartTime(startMs: Long) = editStartTime(startMs)

    fun stopFast(
        nowMs: Long = Clock.System.now().toEpochMilliseconds(),
        mood: Mood? = null,
        note: String? = null,
    ) {
        scope.launch {
            val fast = _settings.value.activeFast?.toActiveFast() ?: return@launch
            val targetMs = (fast.targetHours * 3_600_000).toLong()
            val durationMs = maxOf(0L, nowMs - fast.startMs)
            val eatingHours = resolveEatingHours(fast.planId, _settings.value.customFastingHours)

            sessionRepository.add(
                FastingSession(
                    planId = fast.planId,
                    planLabel = fast.planLabel,
                    targetHours = fast.targetHours,
                    startMs = fast.startMs,
                    endMs = nowMs,
                    durationMs = durationMs,
                    completed = durationMs >= targetMs,
                    createdAt = nowMs,
                    mood = mood,
                    note = note,
                )
            )
            update {
                copy(
                    activeFast = null,
                    eatingWindowStartMs = nowMs,
                    eatingWindowHours = eatingHours,
                )
            }
            triggerAutoSync()
        }
    }

    fun addWater(ml: Int) {
        update {
            val todayEpochDay = Clock.System.now().toEpochMilliseconds() / 86_400_000L
            val currentAmount = if (waterDateEpochDay == todayEpochDay) waterIntakeMl else 0
            val newAmount = (currentAmount + ml).coerceIn(0, 10000)

            if (healthStatus.value.state == HealthPermissionState.CONNECTED && ml > 0) {
                scope.launch {
                    healthSyncService.syncHydration(ml.toDouble(), Clock.System.now().toEpochMilliseconds())
                }
            }

            copy(
                waterIntakeMl = newAmount,
                waterDateEpochDay = todayEpochDay,
            )
        }
        triggerAutoSync()
    }

    fun resetAll() {
        scope.launch {
            val sessions = sessionRepository.observeAll().first()
            val weights = weightRepository.observeAll().first()
            sessionRepository.markDeleted(sessions.map { it.startMs })
            weightRepository.markDeleted(weights.map { it.recordedAt })
            sessionRepository.clearAll()
            weightRepository.clearAll()
            update { AppSettings() }
            triggerAutoSync()
        }
    }

    fun logWeight(weightKg: Double) {
        val nowMs = Clock.System.now().toEpochMilliseconds()
        scope.launch {
            weightRepository.add(
                WeightRecord(
                    weightKg = weightKg,
                    recordedAt = nowMs
                )
            )
            if (healthStatus.value.state == HealthPermissionState.CONNECTED) {
                healthSyncService.syncWeight(weightKg, nowMs)
            }
            triggerAutoSync()
        }
    }

    fun deleteWeight(id: Long) {
        scope.launch {
            weightRepository.delete(id)
            triggerAutoSync()
        }
    }

    fun deleteSession(id: Long) {
        scope.launch {
            sessionRepository.delete(id)
            triggerAutoSync()
        }
    }

    fun updateSession(session: FastingSession) {
        scope.launch {
            sessionRepository.update(session)
            triggerAutoSync()
        }
    }

    fun updateMoodNote(sessionId: Long, mood: Mood?, note: String?) {
        scope.launch {
            sessionRepository.setMoodNote(sessionId, mood, note)
            triggerAutoSync()
        }
    }
}

fun resolveTargetHours(method: MethodId, customHours: Double): Double =
    if (method == MethodId.CUSTOM) customHours else METHODS[method]?.fastingHours ?: 16.0

fun resolveEatingHours(method: MethodId, customHours: Double): Double =
    if (method == MethodId.CUSTOM) (24.0 - customHours).coerceAtLeast(0.0) else METHODS[method]?.eatingHours ?: 8.0

fun resolvePlanLabel(method: MethodId, customHours: Double): String =
    if (method == MethodId.CUSTOM) "Custom ${customHours.toInt()}j" else METHODS[method]?.label ?: "16:8"

fun calculateStreak(sessions: List<FastingSession>): Int {
    if (sessions.isEmpty()) return 0
    val completedSessions = sessions.filter { it.completed }
    if (completedSessions.isEmpty()) return 0

    val days = completedSessions.map { it.endMs / 86_400_000L }.distinct().sortedDescending()
    if (days.isEmpty()) return 0

    val today = Clock.System.now().toEpochMilliseconds() / 86_400_000L
    val firstDay = days.first()
    if (firstDay != today && firstDay != today - 1) {
        return 0
    }

    var streak = 1
    for (i in 0 until days.size - 1) {
        if (days[i] - days[i + 1] == 1L) {
            streak++
        } else if (days[i] == days[i + 1]) {
            continue
        } else {
            break
        }
    }
    return streak
}
