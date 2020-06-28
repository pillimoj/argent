package argent.api

import argent.api.dto.ChecklistItemReq
import argent.api.dto.ChecklistItemRes
import argent.api.dto.ChecklistReq
import argent.api.dto.DeleteItemsReq
import argent.api.dto.SetItemDoneReq
import argent.checklists.ChecklistDataStore
import argent.server.BadRequestException
import argent.server.DataBases
import argent.server.InternalServerError
import argent.server.features.User
import argent.util.extra
import argent.util.logger
import argent.util.pathParam
import argent.util.requireMethod
import argent.util.toGMTDate
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.http.HttpMethod
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable

typealias RouteHandler = suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit

@Serializable
data class MessageResponse(val message: String)

val OkResponse = MessageResponse("OK")

fun handler(method: HttpMethod, block: RouteHandler): RouteHandler = {
    requireMethod(method)
    block(Unit)
}

private val store = ChecklistDataStore(DataBases.Argent.database)

object ApiController {
    val me = handler(HttpMethod.Get) {
        val principal = call.principal<User>() ?: throw InternalServerError("No principal in api handler")
        call.respond(principal)
    }

    object Checklists {
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
            val wife = call.principal<User>()
            val checklists = store.getChecklists()
            call.respond(checklists)
        }

        val get = handler(HttpMethod.Get) {
            val id = pathParam()
            val res = store.getChecklistWithItems(id)
            call.respond(res)
        }

        val deleteItems = handler(HttpMethod.Delete){
            val deleteItemsReq = call.receive<DeleteItemsReq>()
            store.deleteItems(deleteItemsReq.items)
            call.respond(OkResponse)
        }

        val clearDone = handler(HttpMethod.Post){
            val id = pathParam()
            store.clearDone(id)
            call.respond(OkResponse)
        }
    }

    object ChecklistItems {
        val create = handler(HttpMethod.Post) {
            val req = call.receive<ChecklistItemReq>()
            if(!store.hasChecklist(req.checklist)) throw BadRequestException("No checklist with that id")
            val res = store.addItem(req)
            call.respond(ChecklistItemRes(res.id.value, res.title, res.done, res.createdAt.toGMTDate()))
        }

        val delete = handler(HttpMethod.Delete){
            val id = pathParam()
            store.deleteItem(id)
            call.respond(OkResponse)
        }

        val setDone = handler(HttpMethod.Post) {
            val id = pathParam()
            logger.info("Set item done", extra("id" to id))
            val done = call.receive<SetItemDoneReq>().done
            store.setItemDone(id, done)
            call.respond(OkResponse)
        }
    }
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
