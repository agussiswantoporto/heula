package id.heula.`data`.db

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Double
import kotlin.Long
import kotlin.String

public class SessionQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> getAllSessions(mapper: (
    id: Long,
    planId: String,
    planLabel: String,
    targetHours: Double,
    startMs: Long,
    endMs: Long,
    durationMs: Long,
    completed: Long,
    createdAt: Long,
    mood: String?,
    note: String?,
  ) -> T): Query<T> = Query(-1_246_505_440, arrayOf("FastingSessionEntity"), driver, "Session.sq",
      "getAllSessions",
      "SELECT FastingSessionEntity.id, FastingSessionEntity.planId, FastingSessionEntity.planLabel, FastingSessionEntity.targetHours, FastingSessionEntity.startMs, FastingSessionEntity.endMs, FastingSessionEntity.durationMs, FastingSessionEntity.completed, FastingSessionEntity.createdAt, FastingSessionEntity.mood, FastingSessionEntity.note FROM FastingSessionEntity ORDER BY startMs DESC") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getDouble(3)!!,
      cursor.getLong(4)!!,
      cursor.getLong(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!!,
      cursor.getLong(8)!!,
      cursor.getString(9),
      cursor.getString(10)
    )
  }

  public fun getAllSessions(): Query<FastingSessionEntity> = getAllSessions { id, planId, planLabel,
      targetHours, startMs, endMs, durationMs, completed, createdAt, mood, note ->
    FastingSessionEntity(
      id,
      planId,
      planLabel,
      targetHours,
      startMs,
      endMs,
      durationMs,
      completed,
      createdAt,
      mood,
      note
    )
  }

  public fun lastInsertId(): ExecutableQuery<Long> = Query(369_898_082, driver, "Session.sq",
      "lastInsertId", "SELECT last_insert_rowid()") { cursor ->
    cursor.getLong(0)!!
  }

  public fun insertSession(
    planId: String,
    planLabel: String,
    targetHours: Double,
    startMs: Long,
    endMs: Long,
    durationMs: Long,
    completed: Long,
    createdAt: Long,
    mood: String?,
    note: String?,
  ) {
    driver.execute(-2_060_692_315, """
        |INSERT INTO FastingSessionEntity(planId, planLabel, targetHours, startMs, endMs, durationMs, completed, createdAt, mood, note)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 10) {
          bindString(0, planId)
          bindString(1, planLabel)
          bindDouble(2, targetHours)
          bindLong(3, startMs)
          bindLong(4, endMs)
          bindLong(5, durationMs)
          bindLong(6, completed)
          bindLong(7, createdAt)
          bindString(8, mood)
          bindString(9, note)
        }
    notifyQueries(-2_060_692_315) { emit ->
      emit("FastingSessionEntity")
    }
  }

  public fun deleteSession(id: Long) {
    driver.execute(-1_753_957_005, """DELETE FROM FastingSessionEntity WHERE id = ?""", 1) {
          bindLong(0, id)
        }
    notifyQueries(-1_753_957_005) { emit ->
      emit("FastingSessionEntity")
    }
  }

  public fun updateMoodNote(
    mood: String?,
    note: String?,
    id: Long,
  ) {
    driver.execute(-1_620_909_718,
        """UPDATE FastingSessionEntity SET mood = ?, note = ? WHERE id = ?""", 3) {
          bindString(0, mood)
          bindString(1, note)
          bindLong(2, id)
        }
    notifyQueries(-1_620_909_718) { emit ->
      emit("FastingSessionEntity")
    }
  }

  public fun clearSessions() {
    driver.execute(1_427_643_666, """DELETE FROM FastingSessionEntity""", 0)
    notifyQueries(1_427_643_666) { emit ->
      emit("FastingSessionEntity")
    }
  }
}
