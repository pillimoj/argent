package argent.api

import argent.data.checklists.Checklist
import argent.data.checklists.ChecklistAccessType
import argent.data.checklists.ChecklistDataStore
import argent.data.checklists.ChecklistItem
import argent.data.users.User
import argent.data.users.UserDataStore
import argent.data.users.UserRole
import argent.server.BadRequestException
import argent.server.ForbiddenException
import argent.util.pathIdParam
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import java.util.UUID

class ChecklistController(private val checklistDataStore: ChecklistDataStore, private val userDataStore: UserDataStore) {
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

    inner class Checklists {
        val create = authedHandler(HttpMethod.Post) { user ->
            val checklist = Checklist.deserialize(call)
            checklistDataStore.addChecklist(checklist, user)
            call.respond(checklist)
        }

        val delete = authedHandler(HttpMethod.Delete) { user ->
            val id = pathIdParam()
            if (!isOwner(id, user)) {
                throw ForbiddenException()
            }
            checklistDataStore.deleteChecklist(id)
            call.respondOk()
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
            call.respondOk()
        }

        val share = authedHandler(HttpMethod.Post) { user ->
            val checklistId = pathIdParam()
            val shareRequest = ShareRequest.deserialize(call)
            if(!isOwner(checklistId, user)){
                throw ForbiddenException()
            }
            checklistDataStore.addUserAccess(checklistId, shareRequest.userId, shareRequest.accessType)
            call.respondOk()
        }

        val unShare = authedHandler(HttpMethod.Post) { user ->
            val checklistId = pathIdParam()
            val userId = pathIdParam("userId")
            val checklistOwners = userDataStore.getUsersForChecklist(checklistId)
                .filter { it.checklistAccessType == ChecklistAccessType.Owner }
            if(checklistOwners.size == 1 && checklistOwners.first().id == userId){
                throw BadRequestException("Cannot remove lst owner of a list")
            }
            if(!isOwner(checklistId, user)){
                throw ForbiddenException()
            }
            checklistDataStore.removeUserAccess(checklistId, userId)
            call.respondOk()
        }

        val getUsers = authedHandler(HttpMethod.Get){ user ->
            val checklistId = pathIdParam()
            if(!hasAccess(checklistId, user)){
                throw ForbiddenException()
            }
            val users = userDataStore.getUsersForChecklist(checklistId)
            call.respond(users)
        }
    }

    inner class ChecklistItems {
        val create = authedHandler(HttpMethod.Post) { user ->
            val item = ChecklistItem.deserialize(call)
            if (!hasAccess(item.checklist, user)) throw ForbiddenException()
            checklistDataStore.addItem(item)
            call.respondOk()
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
            callContext.call.respondOk()
        }
    }

    val checklists = Checklists()
    val checklistItems = ChecklistItems()
}
