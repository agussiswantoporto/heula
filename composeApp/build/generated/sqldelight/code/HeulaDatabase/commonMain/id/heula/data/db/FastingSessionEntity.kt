package id.heula.`data`.db

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class FastingSessionEntity(
  public val id: Long,
  public val planId: String,
  public val planLabel: String,
  public val targetHours: Double,
  public val startMs: Long,
  public val endMs: Long,
  public val durationMs: Long,
  public val completed: Long,
  public val createdAt: Long,
  public val mood: String?,
  public val note: String?,
)
