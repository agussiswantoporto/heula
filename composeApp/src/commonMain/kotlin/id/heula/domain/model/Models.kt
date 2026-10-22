package id.heula.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class MethodId { FOURTEEN_TEN, SIXTEEN_EIGHT, EIGHTEEN_SIX, TWENTY_FOUR, OMAD, FIVE_TWO, CUSTOM }

@Serializable
enum class Goal { LOSE_WEIGHT, MAINTAIN, FEEL_BETTER, METABOLIC }

@Serializable
enum class Intensity { LIGHT, MODERATE, HEAVY }

@Serializable
enum class Mood { ENERGIK, BIASA, LEMES, LAPAR }

@Serializable
enum class Theme { SYSTEM, LIGHT, DARK }

@Serializable
enum class AppLanguage { INDONESIAN, ENGLISH }

@Serializable
data class ProfilingAnswers(
    val goal: Goal,
    val exercises: Boolean,
    val intensity: Intensity?,
    val heightCm: Double,
    val weightKg: Double,
    val targetWeightKg: Double? = null,
)

@Serializable
data class ActiveFast(
    val startMs: Long,
    val targetHours: Double,
    val planId: MethodId,
    val planLabel: String,
)

@Serializable
data class FastingSession(
    val id: Long = 0,
    val planId: MethodId,
    val planLabel: String,
    val targetHours: Double,
    val startMs: Long,
    val endMs: Long,
    val durationMs: Long,
    val completed: Boolean,
    val createdAt: Long,
    val mood: Mood? = null,
    val note: String? = null,
)

@Serializable
data class WeightRecord(
    val id: Long = 0,
    val weightKg: Double,
    val recordedAt: Long,
)

@Serializable
data class FastingMethod(
    val id: MethodId,
    val label: String,
    val fastingHours: Double,
    val eatingHours: Double,
    val tagline: String,
)

@Serializable
data class Reminders(
    val endReminder: Boolean = true,
    val startReminder: Boolean = false,
)
