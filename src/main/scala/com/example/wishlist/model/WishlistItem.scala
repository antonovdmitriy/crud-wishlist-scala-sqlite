package com.example.wishlist.model

case class namdeplinWishlistItem(
    id: Int,
    name: String,
    description: Option[String],
    price: Option[Double],
    linkToOrder: String
)
