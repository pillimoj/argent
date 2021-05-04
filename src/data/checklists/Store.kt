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
        val checklistIds = db.userAccess.whereEqualTo("userId", user.user.toString()).select("checklist").get().await().parseList<String>()
        return db.checklists
            .whereIn("checklistId", checklistIds.toMutableList())
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
        return db.checklistItems
            .whereEqualTo("checklist", checklistId.toString())
            .get()
            .await()
            .parseList()
    }

    suspend fun addChecklist(checklist: Checklist, user: User) {
        val checklistFuture = db.checklists.document(checklist.id.toString()).set(checklist.storable())
        val accessFuture = db.userAccess.add(UserAccess(checklist.id, user.user, user.name, ChecklistAccessType.Owner).storable())
        checklistFuture.await()
        accessFuture.await()
    }

    suspend fun getItem(itemId: UUID): ChecklistItem? {
        return db.checklistItems.document(itemId.toString()).get().await().parseOne()
    }

    suspend fun addItem(checklistItem: ChecklistItem) {
        db.checklistItems.document(checklistItem.checklist.toString()).set(checklistItem.storable()).await()
    }

    suspend fun setItemDone(checklistItemId: UUID, isDone: Boolean) {
        db.checklistItems.document(checklistItemId.toString()).update(mapOf("done" to isDone)).await()
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun deleteChecklist(@Suppress("UNUSED_PARAMETER") checklistId: UUID) {
        TODO("Not implemented")
    }

    suspend fun clearDone(checklistId: UUID) {
        val deleteFutures = db.checklistItems
            .whereEqualTo("checklist", checklistId.toString())
            .whereEqualTo("done", true)
            .select("checklistItem")
            .get()
            .await()
            .map { it.reference.delete() }
        ApiFutures.allAsList(deleteFutures).await()
    }

    suspend fun getAccessType(checklistId: UUID, user: User): ChecklistAccessType? {
        return db.userAccess
            .whereEqualTo("userId", user.user.toString())
            .whereEqualTo("checklist", checklistId.toString())
            .get()
            .await()
            .parseOne<UserAccess>()
            ?.checklistAccessType
    }

    suspend fun addUserAccess(checklistId: UUID, userId: UUID, accessType: ChecklistAccessType) {
        val userAccess = UserAccess(checklistId, userId, "", accessType)
        db.userAccess.add(userAccess.storable()).await()
        TODO("Implement name")
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
