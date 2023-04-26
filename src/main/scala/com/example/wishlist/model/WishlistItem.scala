package com.example.wishlist.model

case class WishlistItem(
    id: Int,
    name: String,
    description: Option[String],
    price: Option[Double],
    linkToOrder: Option[String]
)
