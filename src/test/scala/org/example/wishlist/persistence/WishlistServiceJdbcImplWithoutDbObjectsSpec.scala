package org.example.wishlist.persistence

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.example.wishlist.controller.WishlistController
import com.example.wishlist.model.WishlistJsonProtocol._
import com.example.wishlist.model.{WishlistItem, WishlistItemInput}
import com.example.wishlist.persistence.{DataSourceBuilder, HikariCPDataSourceBuilder}
import com.example.wishlist.service.{WishlistService, WishlistServiceJdbcImpl}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json._

import java.io.File


class WishlistServiceJdbcImplWithoutDbObjectsSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  def controller: WishlistController = new WishlistController(wishlistService);
  val route: Route = controller.route
  var config: Config = _;

  def wishlistService: WishlistService = {
    config = ConfigFactory.load("application-test.conf")
    val dataSourceBuilder: DataSourceBuilder = new HikariCPDataSourceBuilder
    new WishlistServiceJdbcImpl(dataSourceBuilder.configureDataSource(config))
  }

  override def afterAll(): Unit = {
    super.afterAll()
    val dbFile: File = new File(config.getString("db.dbPath"))
    if (dbFile.exists()) {
      dbFile.delete()
    }
  }

  "WishlistService" should {
    "get error when table is not created" in {
      val entity = HttpEntity(ContentTypes.`application/json`, WishlistItemInput(
        "test item",
        Some("test description"),
        "test link",
        Some(100.0)
      ).toJson.toString)

      // Add an item to the wishlist
      Post("/wishlist", entity) ~> route ~> check {
        status shouldBe StatusCodes.InternalServerError
      }
    }
  }

}
