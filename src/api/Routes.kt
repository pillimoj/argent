package argent.api

import argent.api.controllers.AdminController
import argent.api.controllers.ChatController
import argent.api.controllers.ChecklistController
import argent.api.controllers.GameController
import argent.api.controllers.UsersController
import io.ktor.auth.authenticate
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.websocket.webSocket

fun Route.v1Routes(
    checklistController: ChecklistController,
    usersController: UsersController,
    adminController: AdminController,
    gameController: GameController,
    chatController: ChatController,
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
        route("wishlist-items/{...}") {
            get(notImplementedAuthedHandler(HttpMethod.Get))
            post(notImplementedAuthedHandler(HttpMethod.Get))
            delete(notImplementedAuthedHandler(HttpMethod.Get))
        }
        route("wishlists/{...}") {
            get(notImplementedAuthedHandler(HttpMethod.Get))
            post(notImplementedAuthedHandler(HttpMethod.Get))
            delete(notImplementedAuthedHandler(HttpMethod.Get))
        }
        route("users") {
            get(usersController.getAll)
        }

        route("admin/users") {
            get(adminController.getAllUsers)
            post(adminController.addUser)
            delete("{id}", adminController.deleteUser)
        }
        route("marble-game") {
            get("status", gameController.getStatus)
            post("set-highest-cleared", gameController.setHighestCleared)
        }
        route("chat") {
            webSocket(protocol = null, chatController.chatHandler)
        }
    } // end authenticate
    get("login", usersController.login)
    get("logout", usersController.logout)
}
