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
                get(ApiController.Checklists.get)
                delete(ApiController.Checklists.delete)
                post("clear-done", ApiController.Checklists.clearDone)
            }
        }
        route("checklistitems") {
            post(ApiController.ChecklistItems.create)
            route("{id}") {
                delete(ApiController.ChecklistItems.delete)
                post("done", ApiController.ChecklistItems.setDone)
            }
        }
    }
}
