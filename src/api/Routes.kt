package argent.api

import io.ktor.routing.*

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
