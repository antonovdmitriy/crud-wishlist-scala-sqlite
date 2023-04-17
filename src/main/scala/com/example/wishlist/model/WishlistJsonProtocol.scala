package com.example.wishlist.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

object WishlistJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val wishlistItemFormat: RootJsonFormat[WishlistItem] = jsonFormat5(
    WishlistItem
  )

  implicit val wishlistItemInputFormat: RootJsonFormat[WishlistItemInput] =
    jsonFormat4(WishlistItemInput)
}
