package com.example.wishlist.persistence

import com.typesafe.config.Config

import javax.sql.DataSource
import scala.util.Using

class SqliteDatabaseInitializer extends DatabaseInitializer {
  override def createTablesIfDoNotExist(
      dataSource: DataSource,
      config: Config
  ): Unit = {
    val ddl = config.getString("wishlist.ddl")
    if (ddl.isEmpty)
      throw new IllegalStateException("DDL is empty in configuration")

    Using(dataSource.getConnection) { conn =>
      Using(conn.createStatement()) { statement =>
        try {
          statement.execute(ddl)
        }catch {
          case ex: Exception =>
            ex.printStackTrace()
        }
      }
    }
  }
}
