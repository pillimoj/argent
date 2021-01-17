package argent.api.controllers

import argent.api.authedHandler
import argent.api.respondOk
import argent.api.serialization.UserForSharing
import argent.api.serialization.WishlistShareRequest
import argent.api.serialization.deserialize
import argent.data.wishlists.WishlistDataStore
import argent.data.wishlists.WishlistItem
import argent.server.ForbiddenException
import argent.server.NotFoundException
import argent.util.pathIdParam
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

class WishListController(wishlistDataStore: WishlistDataStore) {
    val getAvailableWishlistsUsers = authedHandler(HttpMethod.Get) { user ->
        val wishlistsUsers = wishlistDataStore.getAvailableWishlistsForUser(user).map { UserForSharing(it) }
        call.respond(wishlistsUsers)
    }

    val addWishListItem = authedHandler(HttpMethod.Post) { user ->
        val wishlistItem = WishlistItem.deserialize(call)
        if (user.id != wishlistItem.user) {
            throw ForbiddenException()
        }
        wishlistDataStore.addItem(wishlistItem)
        call.respondOk()
    }

    val deleteWishListItem = authedHandler(HttpMethod.Delete) { user ->
        val itemId = pathIdParam()
        val wishlistItem = wishlistDataStore.getWishlistItem(itemId) ?: throw NotFoundException()
        if (wishlistItem.user != user.id) {
            throw ForbiddenException()
        }
        wishlistDataStore.deleteItem(wishlistItem.id)
        call.respondOk()
    }

    val takeItem = authedHandler(HttpMethod.Post) { user ->
        val itemId = pathIdParam()
        val wishlistItem = wishlistDataStore.getWishlistItem(itemId) ?: throw NotFoundException()
        if (!wishlistDataStore.userHasAccess(wishlistItem.user, user)) {
            throw ForbiddenException()
        }
        wishlistDataStore.setItemTaken(itemId, user)
        call.respondOk()
    }

    val releaseItem = authedHandler(HttpMethod.Post) { user ->
        val itemId = pathIdParam()
        val wishlistItem = wishlistDataStore.getWishlistItem(itemId) ?: throw NotFoundException()
        if (!wishlistDataStore.userHasAccess(wishlistItem.user, user)) {
            throw ForbiddenException()
        }
        if (user.id != wishlistItem.takenBy) {
            throw ForbiddenException()
        }
        wishlistDataStore.setItemNotTaken(itemId)
        call.respondOk()
    }

    val getItemsForUser = authedHandler(HttpMethod.Get) { user ->
        val wishListUserId = pathIdParam()
        if (!wishlistDataStore.userHasAccess(wishListUserId, user)) {
            throw ForbiddenException()
        }
        val items = wishlistDataStore.getWishesForUser(wishListUserId)
        call.respond(items)
    }

    val getOwnItems = authedHandler(HttpMethod.Get) { user ->
        val items = wishlistDataStore.getWishesForUser(user.id)
        call.respond(items)
    }

    val getUsersWithAccess = authedHandler(HttpMethod.Get) { user ->
        val usersWithAccess = wishlistDataStore.getUsersWithAccessToWishlist(user.id).map { UserForSharing(it) }
        call.respond(usersWithAccess)
    }

    val shareWithUser = authedHandler(HttpMethod.Post) { user ->
        val shareRequest = WishlistShareRequest.deserialize(call)
        wishlistDataStore.shareWishlist(user.id, shareRequest.user)
    }
}
