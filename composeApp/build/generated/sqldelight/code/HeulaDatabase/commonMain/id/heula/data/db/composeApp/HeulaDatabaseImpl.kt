package id.heula.`data`.db.composeApp

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import id.heula.`data`.db.HeulaDatabase
import id.heula.`data`.db.SessionQueries
import id.heula.`data`.db.WeightQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<HeulaDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = HeulaDatabaseImpl.Schema

internal fun KClass<HeulaDatabase>.newInstance(driver: SqlDriver): HeulaDatabase =
    HeulaDatabaseImpl(driver)

private class HeulaDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), HeulaDatabase {
  override val sessionQueries: SessionQueries = SessionQueries(driver)

  override val weightQueries: WeightQueries = WeightQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS FastingSessionEntity (
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    planId TEXT NOT NULL,
          |    planLabel TEXT NOT NULL,
          |    targetHours REAL NOT NULL,
          |    startMs INTEGER NOT NULL,
          |    endMs INTEGER NOT NULL,
          |    durationMs INTEGER NOT NULL,
          |    completed INTEGER NOT NULL,
          |    createdAt INTEGER NOT NULL,
          |    mood TEXT,
          |    note TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS WeightEntity (
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    weightKg REAL NOT NULL,
          |    recordedAt INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
