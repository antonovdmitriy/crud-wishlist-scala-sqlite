package com.example.wishlist.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.example.wishlist.model.WishlistItemInput
import com.example.wishlist.model.WishlistJsonProtocol._
import com.example.wishlist.service.WishlistService

class WishlistController(wishlistService: WishlistService) {
  // Define the routes for the controller
  val route: Route = pathPrefix("wishlist") {
    pathEndOrSingleSlash {
      get {
        val items = wishlistService.getAllItems
        complete(items)
      } ~
        post {
          entity(as[WishlistItemInput]) { itemInput =>
            val item = wishlistService.addItem(itemInput)
            complete(StatusCodes.Created, item)
          }
        }
    } ~
      path(IntNumber) { id =>
        get {
          wishlistService.getItem(id) match {
            case Some(item) => complete(item)
            case None       => complete(StatusCodes.NotFound)
          }
        } ~
          put {
            entity(as[WishlistItemInput]) { itemInput =>
              wishlistService.updateItem(id, itemInput) match {
                case Some(item) => complete(item)
                case None       => complete(StatusCodes.NotFound)
              }
            }
          } ~
          delete {
            wishlistService.deleteItem(id) match {
              case Some(item) => complete(item)
              case None       => complete(StatusCodes.NotFound)
            }
          }
      }
  } ~ pathPrefix("health") {
    get {
      complete(StatusCodes.OK)
    }
  }
}
