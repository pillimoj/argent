package argent.api

import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.apiRoutes(controller: ApiController) {
    get("user", controller.user)
    route("checklists"){
        post(controller.checklists.create)
        get(controller.checklists.getAll)
        route("{id}"){
            get(controller.checklists.get)
            delete(controller.checklists.delete)
        }
    }
    route("checklistitems"){
        post(controller.checklistItems.create)
        route("{id}"){
            delete(controller.checklistItems.delete)
            post("done", controller.checklistItems.setDone)
        }
    }
}
