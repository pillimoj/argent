package argent.data.checklists

import argent.data.users.User
import argent.data.users.UserAccess
import argent.google.ArgentStore
import argent.google.await
import argent.google.parseList
import argent.google.parseOne
import argent.google.storable
import com.google.api.core.ApiFutures
import java.util.UUID

class ChecklistDataStore(private val db: ArgentStore) {

    suspend fun getChecklistsForUser(user: User): List<Checklist> {
        val accessibleChecklistsIds = db.userAccess.whereEqualTo("user", user.user.toString())
            .get()
            .await()
            .parseList<UserAccess>()
            .map { it.checklist.toString() }
        return db.checklists.whereIn("checklist", accessibleChecklistsIds)
            .get()
            .await()
            .parseList()
    }

    suspend fun getChecklist(checklistId: UUID): Checklist? {
        return db.checklists
            .document(checklistId.toString())
            .get()
            .await()
            .parseOne()
    }

    suspend fun getChecklistItems(checklistId: UUID): List<ChecklistItem> {
        return db.checklistItems(checklistId)
            .get()
            .await()
            .parseList()
    }

    suspend fun addChecklist(checklist: Checklist, user: User) {
        val checklistFuture = db.checklists.document(checklist.checklist.toString()).set(checklist.storable())
        val accessFuture = db.userAccess.add(UserAccess(checklist.checklist,
            user.user,
            user.name,
            ChecklistAccessType.Owner).storable())
        checklistFuture.await()
        accessFuture.await()
    }

    suspend fun addChecklistNoAccess(checklist: Checklist) {
        val checklistFuture = db.checklists.document(checklist.checklist.toString()).set(checklist.storable())
        checklistFuture.await()
    }

    suspend fun getItem(checklistId: UUID, itemId: UUID): ChecklistItem? {
        return db.checklistItems(checklistId).document(itemId.toString()).get().await().parseOne()
    }

    suspend fun addItem(checklistId: UUID, checklistItem: ChecklistItem) {
        db.checklistItems(checklistId).document(checklistItem.checklistItem.toString()).set(checklistItem.storable())
            .await()
    }

    suspend fun setItemDone(checklistId: UUID, checklistItemId: UUID, isDone: Boolean) {
        db.checklistItems(checklistId).document(checklistItemId.toString()).update(mapOf("done" to isDone)).await()
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun deleteChecklist(@Suppress("UNUSED_PARAMETER") checklistId: UUID) {
        TODO("Not implemented")
    }

    suspend fun clearDone(checklistId: UUID) {
        val deleteFutures = db.checklistItems(checklistId)
            .whereEqualTo("done", true)
            .select("checklistItem")
            .get()
            .await()
            .map { it.reference.delete() }
        ApiFutures.allAsList(deleteFutures).await()
    }

    suspend fun getAccessType(checklistId: UUID, user: User): ChecklistAccessType? {
        return db.userAccess
            .whereEqualTo("user", user.user.toString())
            .whereEqualTo("checklist", checklistId.toString())
            .get()
            .await()
            .parseOne<UserAccess>()
            ?.checklistAccessType
    }

    suspend fun addUserAccess(userAccess: UserAccess) {
        db.userAccess.add(userAccess.storable()).await()
    }

    suspend fun removeUserAccess(checklistId: UUID, userId: UUID) {
        db.userAccess
            .whereEqualTo("userId", userId.toString())
            .whereEqualTo("checklist", checklistId.toString())
            .get()
            .await()
            .firstOrNull()
            ?.reference
            ?.delete()
            ?.await()
    }
}
