package com.example.wishlist.model

case class WishlistItemInput(
    name: String,
    description: Option[String],
    linkToOrder: Option[String],
    price: Option[Double]
)
