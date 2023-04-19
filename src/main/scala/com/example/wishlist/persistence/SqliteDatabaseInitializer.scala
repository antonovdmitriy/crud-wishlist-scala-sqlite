package com.example.wishlist.persistence

import com.typesafe.config.Config

import java.sql.SQLException
import javax.sql.DataSource
import scala.util.{Try, Using}

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
        Try {
          statement.execute(ddl)
        } recover {
          case ex: SQLException =>
            ex.printStackTrace()
        }
      }
    }
  }
}
