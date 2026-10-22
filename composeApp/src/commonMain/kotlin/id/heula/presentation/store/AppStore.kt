package id.heula.presentation.store

import id.heula.data.repository.SessionRepository
import id.heula.data.repository.SettingsRepository
import id.heula.data.repository.WeightRepository
import id.heula.domain.model.*
import id.heula.domain.usecase.AppSettings
import id.heula.domain.usecase.toActiveFast
import id.heula.domain.usecase.toSerializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class AppStore(
    private val settingsRepository: SettingsRepository,
    private val sessionRepository: SessionRepository,
    private val weightRepository: WeightRepository,
    private val scope: CoroutineScope,
) {
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    val activeFast: StateFlow<ActiveFast?> = settings
        .map { it.activeFast?.toActiveFast() }
        .stateIn(scope, SharingStarted.Eagerly, null)

    val sessions = sessionRepository.observeAll()
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val weights = weightRepository.observeAll()
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        scope.launch {
            settingsRepository.observe().collect { _settings.value = it }
        }
    }

    private fun update(block: AppSettings.() -> AppSettings) {
        scope.launch {
            val updated = _settings.value.block()
            _settings.value = updated
            settingsRepository.save(updated)
        }
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

    fun startFast(nowMs: Long = Clock.System.now().toEpochMilliseconds()) = update {
        copy(
            activeFast = ActiveFast(
                startMs = nowMs,
                targetHours = resolveTargetHours(chosenMethod, customFastingHours),
                planId = chosenMethod,
                planLabel = resolvePlanLabel(chosenMethod, customFastingHours),
            ).toSerializable(),
            eatingWindowStartMs = null, // Close eating window when fasting starts
        )
    }

    fun editStartTime(startMs: Long) = update {
        val fast = activeFast ?: return@update this
        if (startMs > Clock.System.now().toEpochMilliseconds()) return@update this
        copy(activeFast = fast.copy(startMs = startMs))
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
        }
    }

    fun addWater(ml: Int) = update {
        val todayEpochDay = Clock.System.now().toEpochMilliseconds() / 86_400_000L
        val currentAmount = if (waterDateEpochDay == todayEpochDay) waterIntakeMl else 0
        val newAmount = (currentAmount + ml).coerceIn(0, 10000)
        copy(
            waterIntakeMl = newAmount,
            waterDateEpochDay = todayEpochDay,
        )
    }

    fun resetAll() {
        scope.launch {
            sessionRepository.clearAll()
            weightRepository.clearAll()
            update { AppSettings() }
        }
    }

    fun logWeight(weightKg: Double) {
        scope.launch {
            weightRepository.add(
                WeightRecord(
                    weightKg = weightKg,
                    recordedAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    fun deleteWeight(id: Long) {
        scope.launch { weightRepository.delete(id) }
    }

    fun deleteSession(id: Long) {
        scope.launch { sessionRepository.delete(id) }
    }

    fun updateMoodNote(sessionId: Long, mood: Mood?, note: String?) {
        scope.launch { sessionRepository.setMoodNote(sessionId, mood, note) }
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
