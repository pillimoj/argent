package argent.api

import argent.api.controllers.AdminController
import argent.api.controllers.ChecklistController
import argent.api.controllers.GameController
import argent.api.controllers.UsersController
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.v1Routes(
    checklistController: ChecklistController,
    usersController: UsersController,
    adminController: AdminController,
    gameController: GameController,
) {
    authenticate {
        get("me", usersController.me)
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
    } // end authenticate
    get("login", usersController.login)
    get("logout", usersController.logout)
}
