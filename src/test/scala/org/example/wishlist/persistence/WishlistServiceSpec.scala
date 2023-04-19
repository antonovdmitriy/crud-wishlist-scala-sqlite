package org.example.wishlist.persistence

import com.example.wishlist.service.{WishlistService, WishlistServiceImpl}

class WishlistServiceSpec extends WishlistServiceSpecBase {

  override def wishlistService: WishlistService = new WishlistServiceImpl
}
