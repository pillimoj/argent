package argent.google

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FirestoreOptions
import java.util.UUID

class ArgentStore {
    private val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build()
    private val db = firestoreOptions.service

    val users = db.collection("argent-users")
    val userAccess = db.collection("argent-usersAccess")
    val checklists = db.collection("argent-checklists")
    fun checklistItems(id: UUID) = checklists.document(id.toString()).collection("items")
    val chat = db.collection("chat")
}
