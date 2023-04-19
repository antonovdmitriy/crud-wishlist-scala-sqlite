package com.example.wishlist.service

import com.example.wishlist.model.{WishlistItem, WishlistItemInput}

trait WishlistService {
  def getAllItems: Seq[WishlistItem]

  def getItem(id: Int): Option[WishlistItem]

  def addItem(itemInput: WishlistItemInput): WishlistItem

  def updateItem(id: Int, itemInput: WishlistItemInput): Option[WishlistItem]

  def deleteItem(id: Int): Int
}
