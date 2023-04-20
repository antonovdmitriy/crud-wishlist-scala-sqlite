package org.example.wishlist.persistence

import com.example.wishlist.persistence.{DataSourceBuilder, DatabaseInitializer, HikariCPDataSourceBuilder, SqliteDatabaseInitializer}
import com.example.wishlist.service.{WishlistService, WishlistServiceJdbcImpl}
import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import javax.sql.DataSource

class WishlistServiceJdbcImplSpec extends WishlistServiceSpecBase {

  var config: Config                     = _;
  var dataSource: DataSource             = _
  var dbInitializer: DatabaseInitializer = _

  override def wishlistService: WishlistService = {
    config = ConfigFactory.load("application-test.conf")
    val dataSourceBuilder: DataSourceBuilder = new HikariCPDataSourceBuilder
    dataSource = dataSourceBuilder.configureDataSource(config)
    dbInitializer = new SqliteDatabaseInitializer
    new WishlistServiceJdbcImpl(dataSource)
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    dbInitializer.createTablesIfDoNotExist(dataSource, config)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    val dbFile: File = new File(config.getString("db.dbPath"))
    if (dbFile.exists()) {
      dbFile.delete()
    }
  }

}
