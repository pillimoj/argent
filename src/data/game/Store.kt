package argent.data.game

import argent.data.users.User
import argent.util.database.DatabaseQueries
import argent.util.database.asyncConnection
import javax.sql.DataSource

class GameDatastore(private val db: DataSource) : DatabaseQueries {

    suspend fun getStatusForUser(user: User): GameStatus {
        return db.asyncConnection {
            val status = executeQuery(
                """
                    SELECT argent_user, highest_cleared
                    FROM marble_game_status
                    WHERE argent_user = ?
                """.trimIndent(),
                listOf(user.id),
                parse { GameStatus(it) }
            )
            if (status == null) {
                createGameStatus(GameStatus(user = user.id, highestCleared = 0))
                GameStatus(user = user.id, highestCleared = 0)
            } else {
                status
            }
        }
    }

    suspend fun setHighestClearedForUser(user: User, newHighest: Int) {
        db.asyncConnection {
            executeUpdate(
                """
                UPDATE  marble_game_status
                SET highest_cleared = ?
                WHERE argent_user = ?
                """.trimIndent(),
                listOf(newHighest, user.id)
            )
        }
    }

    private suspend fun createGameStatus(gameStatus: GameStatus) {
        return db.asyncConnection {
            executeUpdate(
                """
                INSERT INTO marble_game_status (
                    argent_user,
                    highest_cleared
                )
                VALUES (?,?)
                """.trimIndent(),
                listOf(
                    gameStatus.user,
                    gameStatus.highestCleared
                )
            )
        }
    }
}
