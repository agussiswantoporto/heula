package id.heula.`data`.db

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Double
import kotlin.Long

public class WeightQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> getAllWeights(mapper: (
    id: Long,
    weightKg: Double,
    recordedAt: Long,
  ) -> T): Query<T> = Query(486_646_878, arrayOf("WeightEntity"), driver, "Weight.sq",
      "getAllWeights",
      "SELECT WeightEntity.id, WeightEntity.weightKg, WeightEntity.recordedAt FROM WeightEntity ORDER BY recordedAt ASC") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getDouble(1)!!,
      cursor.getLong(2)!!
    )
  }

  public fun getAllWeights(): Query<WeightEntity> = getAllWeights { id, weightKg, recordedAt ->
    WeightEntity(
      id,
      weightKg,
      recordedAt
    )
  }

  public fun lastInsertId(): ExecutableQuery<Long> = Query(1_671_567_452, driver, "Weight.sq",
      "lastInsertId", "SELECT last_insert_rowid()") { cursor ->
    cursor.getLong(0)!!
  }

  public fun insertWeight(weightKg: Double, recordedAt: Long) {
    driver.execute(-590_260_093, """INSERT INTO WeightEntity(weightKg, recordedAt) VALUES (?, ?)""",
        2) {
          bindDouble(0, weightKg)
          bindLong(1, recordedAt)
        }
    notifyQueries(-590_260_093) { emit ->
      emit("WeightEntity")
    }
  }

  public fun deleteWeight(id: Long) {
    driver.execute(-2_104_386_059, """DELETE FROM WeightEntity WHERE id = ?""", 1) {
          bindLong(0, id)
        }
    notifyQueries(-2_104_386_059) { emit ->
      emit("WeightEntity")
    }
  }

  public fun clearWeights() {
    driver.execute(-238_107_680, """DELETE FROM WeightEntity""", 0)
    notifyQueries(-238_107_680) { emit ->
      emit("WeightEntity")
    }
  }
}
