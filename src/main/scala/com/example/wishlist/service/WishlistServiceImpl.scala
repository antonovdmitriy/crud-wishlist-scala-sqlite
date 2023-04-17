package com.example.wishlist.service

import com.example.wishlist.model.{WishlistItem, WishlistItemInput}

import scala.collection.mutable.ListBuffer

class WishlistServiceImpl extends WishlistService {

  private val items = ListBuffer[WishlistItem]()

  override def getAllItems: Seq[WishlistItem] = items.toList

  override def getItem(id: Int): Option[WishlistItem] = items.find(_.id == id)

  override def addItem(itemInput: WishlistItemInput): WishlistItem = {
    val newItem = WishlistItem(
      items.length + 1,
      itemInput.name,
      itemInput.description,
      itemInput.price,
      itemInput.linkToOrder
    )
    items += newItem
    newItem
  }

  override def updateItem(
      id: Int,
      itemInput: WishlistItemInput
  ): Option[WishlistItem] = {
    val index = items.indexWhere(_.id == id)
    if (index != -1) {
      val updatedItem = WishlistItem(
        id,
        itemInput.name,
        itemInput.description,
        itemInput.price,
        itemInput.linkToOrder
      )
      items.update(index, updatedItem)
      Some(updatedItem)
    } else {
      None
    }
  }

  override def deleteItem(id: Int): Option[WishlistItem] = {
    val item = items.find(_.id == id)
    items -= item.getOrElse(return None)
    item
  }

}
