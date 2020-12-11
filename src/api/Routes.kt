package argent.api

import io.ktor.auth.authenticate
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.v1Routes() {
    authenticate {
        get("me", ApiController.me)
        route("checklists") {
            post(ApiController.Checklists.create)
            get(ApiController.Checklists.getAll)
            route("{id}") {
                get(ApiController.Checklists.getItems)
                delete(ApiController.Checklists.delete)
                post("clear-done", ApiController.Checklists.clearDone)
                post("share", ApiController.Checklists.share)
                post("unshare/{userId}", ApiController.Checklists.unShare)
                get("users", ApiController.Checklists.getUsers)
            }
        }
        route("checklistitems") {
            post(ApiController.ChecklistItems.create)
            route("{id}") {
                post("done", ApiController.ChecklistItems.setDone)
                post("not-done", ApiController.ChecklistItems.setNotDone)
            }
        }
        route("users"){
            get(ApiController.Users.getAll)
        }

        route("admin"){
            get("users", AdminController.getAllUsers)
        }
    } // end authenticate
    get("login", ApiController.Users.login)
}
