package org.example.wishlist.persistence

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.example.wishlist.controller.WishlistController
import com.example.wishlist.model.WishlistJsonProtocol._
import com.example.wishlist.model.{WishlistItem, WishlistItemInput}
import com.example.wishlist.service.WishlistService
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AnyWordSpec
import spray.json._

trait WishlistServiceSpecBase extends AnyWordSpec with Matchers with ScalatestRouteTest {

  def wishlistService: WishlistService // Abstract method to be implemented in the child classes
  val route: Route = new WishlistController(wishlistService).route

  "WishlistService" should {
    "get all items from the wishlist" in {
      val itemInput: WishlistItemInput = buildValidWishListInputItem
      val entity                       = HttpEntity(ContentTypes.`application/json`, itemInput.toJson.toString)

      // Add an item to the wishlist
      Post("/wishlist", entity) ~> route ~> check {
        status shouldBe StatusCodes.Created
      }

      // Get all items from the wishlist
      Get("/wishlist") ~> route ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`
        val items = responseAs[Seq[WishlistItem]]
        items should have size 1
        items.head.name shouldBe itemInput.name
        items.head.description shouldBe itemInput.description
        items.head.linkToOrder shouldBe itemInput.linkToOrder
        items.head.price shouldBe itemInput.price
      }
    }

    "add an item to the wishlist" in {
      val itemInput: WishlistItemInput = buildValidWishListInputItem
      val entity =
        HttpEntity(ContentTypes.`application/json`, itemInput.toJson.toString)

      Post("/wishlist", entity) ~> route ~> check {
        status shouldBe StatusCodes.Created
        contentType shouldBe ContentTypes.`application/json`
        val responseItem = responseAs[WishlistItem]
        responseItem.name shouldBe itemInput.name
        responseItem.description shouldBe itemInput.description
        responseItem.linkToOrder shouldBe itemInput.linkToOrder
        responseItem.price shouldBe itemInput.price
      }
    }

    "get an item from the wishlist" in {
      val itemInput: WishlistItemInput = buildValidWishListInputItem
      val entity =
        HttpEntity(ContentTypes.`application/json`, itemInput.toJson.toString)

      Post("/wishlist", entity) ~> route ~> check {
        val responseItem = responseAs[WishlistItem]

        Get(s"/wishlist/${responseItem.id}") ~> route ~> check {
          status shouldBe StatusCodes.OK
          contentType shouldBe ContentTypes.`application/json`
          val retrievedItem = responseAs[WishlistItem]
          retrievedItem shouldBe responseItem
        }
      }
    }

    "update an item in the wishlist" in {
      val itemInput: WishlistItemInput = buildValidWishListInputItem
      val entity =
        HttpEntity(ContentTypes.`application/json`, itemInput.toJson.toString)

      Post("/wishlist", entity) ~> route ~> check {
        val responseItem = responseAs[WishlistItem]

        val updatedItemInput = WishlistItemInput(
          "updated item",
          Some("updated description"),
          "updated link",
          Some(200.0)
        )
        val updateEntity = HttpEntity(
          ContentTypes.`application/json`,
          updatedItemInput.toJson.toString
        )

        Put(s"/wishlist/${responseItem.id}", updateEntity) ~> route ~> check {
          status shouldBe StatusCodes.OK
          contentType shouldBe ContentTypes.`application/json`
          val updatedItem = responseAs[WishlistItem]
          updatedItem.name shouldBe updatedItemInput.name
          updatedItem.description shouldBe updatedItemInput.description
          updatedItem.linkToOrder shouldBe updatedItemInput.linkToOrder
          updatedItem.price shouldBe updatedItemInput.price
        }
      }
    }

    "delete an item from the wishlist" in {
      val itemInput: WishlistItemInput = buildValidWishListInputItem
      val entity                       = HttpEntity(ContentTypes.`application/json`, itemInput.toJson.toString)

      Post("/wishlist", entity) ~> route ~> check {
        val responseItem = responseAs[WishlistItem]

        Delete(s"/wishlist/${responseItem.id}") ~> route ~> check {
          status shouldBe StatusCodes.OK
          responseEntity.toStrict(2.seconds).map(_.data.utf8String).futureValue shouldBe "1"
        }
      }
    }
  }

  private def buildValidWishListInputItem = {
    WishlistItemInput(
      "test item",
      Some("test description"),
      "test link",
      Some(100.0)
    )
  }
}
