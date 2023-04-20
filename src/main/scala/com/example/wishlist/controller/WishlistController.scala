package com.example.wishlist.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, ExceptionHandler, RejectionHandler, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.example.wishlist.model.WishlistItemInput
import com.example.wishlist.model.WishlistJsonProtocol._
import com.example.wishlist.service.WishlistService
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

class WishlistController(wishlistService: WishlistService) {

  // Custom rejection handler
  val rejectionHandler: RejectionHandler = corsRejectionHandler.withFallback(RejectionHandler.default)

  // Custom exception handler
  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(StatusCodes.NotFound, "Cannot find resource")
    case e: ClassCastException =>
      complete(StatusCodes.NotFound, "Incorrect resource returned")
    case e: IllegalArgumentException =>
      complete(StatusCodes.BadRequest, "Illegal argument passed")
    case e: RuntimeException =>
      complete(StatusCodes.InternalServerError, e.getMessage)
  }

  val handleErrors: Directive[Unit] = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)

  // Define the routes for the controller
  val route: Route =
    cors() {
      handleErrors {
        pathPrefix("wishlist") {po
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
                  case None => complete(StatusCodes.NotFound)
                }
              } ~
                put {
                  entity(as[WishlistItemInput]) { itemInput =>
                    wishlistService.updateItem(id, itemInput) match {
                      case Some(item) => complete(item)
                      case None => complete(StatusCodes.NotFound)
                    }
                  }
                } ~
                delete {
                  wishlistService.deleteItem(id) match {
                    case 1 => complete("1")
                    case 0 => complete(StatusCodes.NotFound, "0")
                  }
                }
            }
        } ~ pathPrefix("health") {
          get {
            complete(StatusCodes.OK)
          }
        }
      }
    }

  // Define a custom exception handler
  def customExceptionHandler: ExceptionHandler = ExceptionHandler { case ex: Exception =>
    println(s"Exception caught in WishlistController: ${ex.getMessage}")
    ex.printStackTrace()
    complete(StatusCodes.InternalServerError, s"Internal Server Error: ${ex.getMessage}")
  }
}
