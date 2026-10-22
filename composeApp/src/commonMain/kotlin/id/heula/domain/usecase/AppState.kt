package id.heula.domain.usecase

import id.heula.domain.model.*
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val onboarded: Boolean = false,
    val profiling: ProfilingAnswers? = null,
    val chosenMethod: MethodId = MethodId.SIXTEEN_EIGHT,
    val customFastingHours: Double = 16.0,
    val dailyGoalHours: Double = 16.0,
    val theme: Theme = Theme.SYSTEM,
    val reminders: Reminders = Reminders(),
    val activeFast: SerializableActiveFast? = null,
    val eatingWindowStartMs: Long? = null,
    val eatingWindowHours: Double = 8.0,
    val waterIntakeMl: Int = 0,
    val waterDateEpochDay: Long = 0,
    val language: AppLanguage = AppLanguage.INDONESIAN,
)

@Serializable
data class SerializableActiveFast(
    val startMs: Long,
    val targetHours: Double,
    val planId: MethodId,
    val planLabel: String,
)

fun SerializableActiveFast.toActiveFast() = ActiveFast(startMs, targetHours, planId, planLabel)
fun ActiveFast.toSerializable() = SerializableActiveFast(startMs, targetHours, planId, planLabel)
