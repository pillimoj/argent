package argent.api

import argent.api.serialization.deserialize
import argent.data.wishlists.WishlistDataStore
import argent.data.wishlists.WishlistItem
import argent.server.BadRequestException
import argent.util.pathIdParam
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

class WishListController(wishlistDataStore: WishlistDataStore) {
    val addWishListItem = authedHandler(HttpMethod.Post) { user ->
        val wishlistItem = WishlistItem.deserialize(call)
        if (user.id != wishlistItem.user) {
            throw BadRequestException("Cannot add items to another users wishlist")
        }
        wishlistDataStore.addItem(wishlistItem)
        call.respondOk()
    }

    val takeItem = authedHandler(HttpMethod.Post) { user ->
        val itemId = pathIdParam()
        wishlistDataStore.setItemTaken(itemId, user)
        call.respondOk()
    }

    val getItems = authedHandler(HttpMethod.Get) {
        val userId = pathIdParam()
        val items = wishlistDataStore.getWishesForUser(userId)
        call.respond(items)
    }

    val getOwnItems = authedHandler(HttpMethod.Get) { user ->
        val items = wishlistDataStore.getWishesForUser(user.id)
        call.respond(items)
    }
}
