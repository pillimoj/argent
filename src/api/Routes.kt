package argent.api

import argent.api.controllers.AdminController
import argent.api.controllers.ChecklistController
import argent.api.controllers.UsersController
import argent.api.controllers.WishListController
import io.ktor.auth.authenticate
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.v1Routes(
    checklistController: ChecklistController,
    wishListController: WishListController,
    usersController: UsersController,
    adminController: AdminController
) {
    authenticate {
        get("me", checklistController.me)
        route("checklists") {
            post(checklistController.checklists.create)
            get(checklistController.checklists.getAll)
            route("{id}") {
                get(checklistController.checklists.getById)
                get("items", checklistController.checklists.getItems)
                delete(checklistController.checklists.delete)
                post("clear-done", checklistController.checklists.clearDone)
                post("share", checklistController.checklists.share)
                post("unshare/{userId}", checklistController.checklists.unShare)
                get("users", checklistController.checklists.getUsers)
            }
        }
        route("checklistitems") {
            post(checklistController.checklistItems.create)
            route("{id}") {
                post("done", checklistController.checklistItems.setDone)
                post("not-done", checklistController.checklistItems.setNotDone)
            }
        }
        route("wishlist-items") {
            post(wishListController.addWishListItem)
            route("{id}") {
                delete(wishListController.deleteWishListItem)
                post(wishListController.editWishlistItem)
                post("take", wishListController.takeItem)
                post("release", wishListController.releaseItem)
            }
        }
        route("wishlists") {
            get(wishListController.getAvailableWishlistsUsers)
            get("me", wishListController.getOwnItems)
            get("shared-with", wishListController.getUsersWithAccess)
            post("share", wishListController.shareWithUser)
            get("{id}", wishListController.getItemsForUser)
        }
        route("users") {
            get(usersController.getAll)
        }

        route("admin/users") {
            get(adminController.getAllUsers)
            post(adminController.addUser)
            delete("{id}", adminController.deleteUser)
        }
    } // end authenticate
    get("login", usersController.login)
    get("logout", usersController.logout)
}
