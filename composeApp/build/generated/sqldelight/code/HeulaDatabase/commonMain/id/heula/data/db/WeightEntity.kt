package id.heula.`data`.db

import kotlin.Double
import kotlin.Long

public data class WeightEntity(
  public val id: Long,
  public val weightKg: Double,
  public val recordedAt: Long,
)
