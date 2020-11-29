package argent.api

import argent.data.checklists.Checklist
import argent.data.checklists.ChecklistAccessType
import argent.data.checklists.ChecklistDataStore
import argent.data.checklists.ChecklistItem
import argent.data.users.User
import argent.data.users.UserDataStore
import argent.data.users.UserRole
import argent.server.BadRequestException
import argent.server.DataBases
import argent.server.ForbiddenException
import argent.util.pathIdParam
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import java.util.UUID

private val checklistDataStore = ChecklistDataStore(DataBases.Argent.dbPool)
private val userDataStore = UserDataStore(DataBases.Argent.dbPool)

object ApiController {
    val me = authedHandler(HttpMethod.Get) { user ->
        call.respond(user)
    }

    suspend fun isOwner(checklistId: UUID, user: User): Boolean {
        return user.role == UserRole.Admin || checklistDataStore.getAccessType(
            checklistId,
            user
        ) == ChecklistAccessType.Owner
    }

    suspend fun hasAccess(checklistId: UUID, user: User): Boolean {
        return checklistDataStore.getAccessType(checklistId, user) != null
    }

    object Checklists {
        val create = authedHandler(HttpMethod.Post) { user ->
            val checklist = Checklist.marshall(call)
            checklistDataStore.addChecklist(checklist, user)
            call.respond(checklist)
        }

        val delete = authedHandler(HttpMethod.Delete) { user ->
            val id = pathIdParam()
            if (!isOwner(id, user)) {
                throw ForbiddenException()
            }
            checklistDataStore.deleteChecklist(id)
            call.respond(OkResponse)
        }

        val getAll = authedHandler(HttpMethod.Get) { user ->
            val checklists = checklistDataStore.getChecklistsForUser(user)
            call.respond(checklists)
        }

        val getItems = authedHandler(HttpMethod.Get) { user ->
            val id = pathIdParam()
            if (!hasAccess(id, user)) {
                throw ForbiddenException()
            }
            val items = checklistDataStore.getChecklistItems(id)
            call.respond(items)
        }

        val clearDone = authedHandler(HttpMethod.Post) { user ->
            val id = pathIdParam()
            if (!hasAccess(id, user)) {
                throw ForbiddenException()
            }
            checklistDataStore.clearDone(id)
            call.respond(OkResponse)
        }
    }

    object ChecklistItems {
        val create = authedHandler(HttpMethod.Post) { user ->
            val item = ChecklistItem.marshall(call)
            if (!hasAccess(item.checklist, user)) throw ForbiddenException()
            checklistDataStore.addItem(item)
            call.respond(OkResponse)
        }

        val setDone = authedHandler(HttpMethod.Post) { user ->
            setItemStatus(this, user, true)
        }
        val setNotDone = authedHandler(HttpMethod.Post) { user ->
            setItemStatus(this, user, false)
        }

        private suspend fun setItemStatus(callContext: CallContext, user: User, done: Boolean){
            val id = callContext.pathIdParam()
            val item = checklistDataStore.getItem(id)
            if(item == null || !hasAccess(item.checklist, user)){
                throw BadRequestException()
            }
            checklistDataStore.setItemDone(id, done)
            callContext.call.respond(OkResponse)
        }
    }
}
