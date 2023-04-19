package com.example.wishlist.persistence

import com.typesafe.config.Config

import javax.sql.DataSource
trait DatabaseInitializer {
  def createTablesIfDoNotExist(dataSource: DataSource, config: Config): Unit
}
