package com.example.wishlist.persistence

import com.typesafe.config.Config

import javax.sql.DataSource

trait DataSourceBuilder {
  def configureDataSource(config: Config): DataSource
}