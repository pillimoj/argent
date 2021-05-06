package argent.data.game

import argent.data.users.User
import argent.google.ArgentStore
import argent.google.await
import argent.server.InternalServerError

class GameDatastore(private val db: ArgentStore) {

    suspend fun getStatusForUser(user: User): GameStatus {
        val userDocRef = db.users.document(user.user.toString()).get().await()
        if (!userDocRef.exists()) throw InternalServerError("No user found in db when getting game status for user")
        val highestCleared = userDocRef.get("highestCleared", Int::class.java)
        return GameStatus(user.user, highestCleared ?: 0)
    }

    suspend fun setHighestClearedForUser(user: User, newHighest: Int) {
        db.users.document(user.user.toString()).update("highestCleared", newHighest).await()
    }
}
