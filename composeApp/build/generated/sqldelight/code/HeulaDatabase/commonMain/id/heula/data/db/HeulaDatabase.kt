package id.heula.`data`.db

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import id.heula.`data`.db.composeApp.newInstance
import id.heula.`data`.db.composeApp.schema
import kotlin.Unit

public interface HeulaDatabase : Transacter {
  public val sessionQueries: SessionQueries

  public val weightQueries: WeightQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = HeulaDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): HeulaDatabase =
        HeulaDatabase::class.newInstance(driver)
  }
}
