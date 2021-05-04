package argent.google

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FirestoreOptions

class ArgentStore {
    private val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
        .setProjectId("grimsborn-firestore-test"/*Config.googleProject*/)
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build()
    private val db = firestoreOptions.service

    val users = db.collection("argent-users")
    val userAccess = db.collection("argent-usersAccess")
    val checklists = db.collection("argent-checklists")
    val checklistItems = db.collection("argent-checklistItems")
}
