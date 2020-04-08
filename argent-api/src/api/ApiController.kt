package argent.api

import argent.checklists.ChecklistDataStore
import argent.iap.IAPUserKey
import argent.util.BadRequestException
import argent.util.e
import argent.util.logger
import argent.util.pathParam
import argent.util.requireMethod
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext

typealias RouteHandler = suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit

data class MessageResponse(val message: String)

val OkResponse = MessageResponse("OK")

fun handler(method: HttpMethod, block: RouteHandler): RouteHandler = {
    requireMethod(method)
    block(Unit)
}

class ApiController(store: ChecklistDataStore) {
    val user = handler(HttpMethod.Get) {
        val iapUser = call.attributes[IAPUserKey]
        call.respond(iapUser)
    }

    class Checklists(private val store: ChecklistDataStore) {
        val create = handler(HttpMethod.Post) {
            val req = call.receive<ChecklistReq>()
            val res = store.addChecklist(req)
            call.respond(res)
        }

        val delete= handler(HttpMethod.Delete) {
            val id = pathParam()
            store.deleteChecklist(id)
            call.respond(OkResponse)
        }

        val getAll = handler(HttpMethod.Get) {
            val checklists = store.getChecklists()
            call.respond(checklists)
        }

        val get = handler(HttpMethod.Get) {
            val id = pathParam()
            val res = store.getChecklistWithItems(id)
            call.respond(res)
        }
    }

    class ChecklistItems(private val store: ChecklistDataStore) {
        val create = handler(HttpMethod.Post) {
            val req = call.receive<ChecklistItemReq>()
            if(!store.hasChecklist(req.checklist)) throw BadRequestException("No checklist with that id")
            val res = store.addItem(req)
            call.respond(ChecklistItemRes(res))
        }

        val delete = handler(HttpMethod.Delete){
            val id = pathParam()
            store.deleteItem(id)
            call.respond(OkResponse)
        }

        val setDone = handler(HttpMethod.Post) {
            val id = pathParam()
            logger.info("Set item done", e("id" to id))
            val done = call.receive<SetItemDoneReq>().done
            store.setItemDone(id, done)
            call.respond(OkResponse)
        }
    }

    val checklists = Checklists(store)
    val checklistItems = ChecklistItems(store)
}

object UtilController {
    val ping = handler(HttpMethod.Get) {
        call.respond("Pong")
    }

    val fail = handler(HttpMethod.Get) {
        throw Exception("Fail requested")
    }

    val healthCheck = handler(HttpMethod.Get) {
        call.respond("OK")
    }
}
