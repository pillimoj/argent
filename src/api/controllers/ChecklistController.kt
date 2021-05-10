package argent.api.controllers

import argent.api.CallContext
import argent.api.authedHandler
import argent.api.respondOk
import argent.api.serialization.ShareRequest
import argent.api.serialization.deserialize
import argent.data.checklists.Checklist
import argent.data.checklists.ChecklistAccessType
import argent.data.checklists.ChecklistDataStore
import argent.data.checklists.ChecklistItem
import argent.data.users.User
import argent.data.users.UserAccess
import argent.data.users.UserDataStore
import argent.data.users.UserRole
import argent.server.BadRequestException
import argent.server.ForbiddenException
import argent.server.NotFoundException
import argent.util.WithLogger
import argent.util.extra
import argent.util.logger
import argent.util.pathIdParam
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import java.util.UUID

class ChecklistController(
    private val checklistDataStore: ChecklistDataStore,
    private val userDataStore: UserDataStore,
) : WithLogger {
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
            logger.info("Creating checklist", extra("checklist" to checklist))
            checklistDataStore.addChecklist(checklist, user)
            call.respond(checklist)
        }

        val delete = authedHandler(HttpMethod.Delete) { user ->
            val id = pathIdParam()
            logger.info("deleting checklist", extra("checklistId" to id))
            if (!isOwner(id, user)) {
                throw ForbiddenException()
            }
            checklistDataStore.deleteChecklist(id)
            call.respondOk()
        }

        val getAll = authedHandler(HttpMethod.Get) { user ->
            logger.info("getting all checklists")
            val checklists = checklistDataStore.getChecklistsForUser(user)
            call.respond(checklists)
        }

        val getById = authedHandler(HttpMethod.Get) { user ->
            val id = pathIdParam()
            logger.info("getting checklist", extra("checklistId" to id))
            if (!hasAccess(id, user)) {
                throw ForbiddenException()
            }
            val checklist = checklistDataStore.getChecklist(id) ?: throw NotFoundException("No such checklist: $id")
            call.respond(checklist)
        }

        val getItems = authedHandler(HttpMethod.Get) { user ->
            val id = pathIdParam()
            logger.info("getting checklist items", extra("checklistId" to id))
            if (!hasAccess(id, user)) {
                throw ForbiddenException()
            }
            val items = checklistDataStore.getChecklistItems(id)
            call.respond(items)
        }

        val clearDone = authedHandler(HttpMethod.Post) { user ->
            val id = pathIdParam()
            logger.info("clearing done checklist items", extra("checklistId" to id))
            if (!hasAccess(id, user)) {
                throw ForbiddenException()
            }
            checklistDataStore.clearDone(id)
            call.respondOk()
        }

        val share = authedHandler(HttpMethod.Post) { user ->
            val checklistId = pathIdParam()
            val shareRequest = ShareRequest.deserialize(call)
            logger.info("sharing checklist", extra("checklistId" to checklistId, "shareRequest" to shareRequest))
            if (!isOwner(checklistId, user)) {
                throw ForbiddenException()
            }
            val userToShareWith =
                userDataStore.getUser(shareRequest.user) ?: throw BadRequestException("No such user to share with")
            checklistDataStore.addUserAccess(
                UserAccess(
                    checklist = checklistId,
                    user = shareRequest.user,
                    name = userToShareWith.name,
                    checklistAccessType = shareRequest.accessType
                )
            )
            call.respondOk()
        }

        val unShare = authedHandler(HttpMethod.Post) { user ->
            val checklistId = pathIdParam()
            val userId = pathIdParam("userId")
            logger.info("Un-sharing checklist", extra("userToUnshareWith" to userId))
            val checklistOwners = userDataStore.getUsersForChecklist(checklistId)
                .filter { it.checklistAccessType == ChecklistAccessType.Owner }
            if (checklistOwners.size == 1 && checklistOwners.first().user == userId) {
                throw BadRequestException("Cannot remove last owner of a list")
            }
            if (!isOwner(checklistId, user)) {
                throw ForbiddenException()
            }
            checklistDataStore.removeUserAccess(checklistId, userId)
            call.respondOk()
        }

        val getUsers = authedHandler(HttpMethod.Get) { user ->
            val checklistId = pathIdParam()
            logger.info("getting users for checklist", extra("checklistId" to checklistId))
            if (!hasAccess(checklistId, user)) {
                throw ForbiddenException()
            }
            val users = userDataStore.getUsersForChecklist(checklistId)
            call.respond(users)
        }
    }

    inner class ChecklistItems {
        val create = authedHandler(HttpMethod.Post) { user ->
            val checklistId = pathIdParam()
            val item = ChecklistItem.deserialize(call)
            logger.info("adding item to checklist", extra("checklistId" to checklistId, "item" to item))
            if (!hasAccess(checklistId, user)) throw ForbiddenException()
            checklistDataStore.addItem(checklistId, item)
            call.respondOk()
        }

        val setDone = authedHandler(HttpMethod.Post) { user ->
            setItemStatus(this, user, true)
        }
        val setNotDone = authedHandler(HttpMethod.Post) { user ->
            setItemStatus(this, user, false)
        }

        private suspend fun setItemStatus(callContext: CallContext, user: User, done: Boolean) {
            val checklistId = callContext.pathIdParam()
            val itemId = callContext.pathIdParam("item-id")
            logger.info(
                "setting checklist item status",
                extra("checklistId" to checklistId, "itemId" to itemId, "newStatus" to done)
            )
            val item = checklistDataStore.getItem(checklistId, itemId)
            if (item == null || !hasAccess(checklistId, user)) {
                throw BadRequestException()
            }
            checklistDataStore.setItemDone(checklistId, itemId, done)
            callContext.call.respondOk()
        }
    }

    val checklists = Checklists()
    val checklistItems = ChecklistItems()
}
