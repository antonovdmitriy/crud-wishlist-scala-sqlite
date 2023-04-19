package com.example.wishlist.persistence;

import com.typesafe.config.Config
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import javax.sql.DataSource

class HikariCPDataSourceBuilder extends DataSourceBuilder {

  override def configureDataSource(config: Config): DataSource = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl(config.getString("db.jdbcUrl"))
    hikariConfig.setUsername(config.getString("db.username"))
    hikariConfig.setPassword(config.getString("db.password"))
    hikariConfig.setMaximumPoolSize(config.getInt("db.maximumPoolSize"))
    new HikariDataSource(hikariConfig)
  }
}
