package argent.data.users

import argent.google.ArgentStore
import argent.google.await
import argent.google.parseList
import argent.google.parseOne
import argent.google.storable
import com.google.api.core.ApiFutures
import java.util.UUID

class UserDataStore(private val db: ArgentStore) {
    suspend fun getUserForEmail(email: String): User? {
        return db.users.whereEqualTo("email", email).get().await().parseOne()
    }

    suspend fun getUser(userId: UUID): User? {
        return db.users.document(userId.toString()).get().await().parseOne()
    }

    suspend fun getUsersForChecklist(checklistId: UUID): List<UserAccess> {
        val userAccesses = db.userAccess.whereEqualTo("checklist", checklistId).select("user").get().await().parseList<String>().toSet()
        if (userAccesses.isEmpty()) return emptyList()
        return db.users.whereIn("user", userAccesses.toMutableList()).get().await().parseList()
    }

    suspend fun getAllUsers(): List<User> {
        return db.users.get().await().parseList()
    }

    suspend fun addUser(user: User) {
        db.users.document(user.user.toString()).set(user.storable()).await()
    }

    suspend fun deleteUser(userId: UUID) {
        val userAccessDocRefs = db.userAccess.whereEqualTo("user", userId.toString()).select("user").get().await().map { it.reference }
        val deleteAccessFutures = userAccessDocRefs.map { it.delete() }
        ApiFutures.allAsList(deleteAccessFutures).await()
        db.users.document(userId.toString()).delete().await()
    }
}
