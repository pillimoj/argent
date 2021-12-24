package argent.data.users

import argent.util.database.DatabaseQueries
import argent.util.database.asyncConnection
import java.util.UUID
import javax.sql.DataSource

class UserDataStore(private val db: DataSource) : DatabaseQueries {
    suspend fun getUserForEmail(email: String): User? {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT
                    id,
                    name,
                    email,
                    role
                FROM argent_users
                WHERE email = ?
                """.trimIndent(),
                listOf(email),
                parse { User(it) }
            )
        }
    }

    suspend fun getUser(userId: UUID): User? {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT
                    id,
                    name,
                    email,
                    role
                FROM argent_users
                WHERE id = ?
                """.trimIndent(),
                listOf(userId),
                parse { User(it) }
            )
        }
    }

    suspend fun getUsersForChecklist(checklistId: UUID): List<UserAccess> {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT
                    id,
                    name,
                    access_type
                FROM argent_users u
                LEFT JOIN checklist_access ca
                ON ca.argent_user = u.id
                WHERE ca.checklist = ?
                """.trimIndent(),
                listOf(checklistId),
                parseList { UserAccess(it) }
            )
        }
    }

    suspend fun getAllUsers(): List<User> {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT
                    id,
                    name,
                    email,
                    role
                FROM argent_users u
                """.trimIndent(),
                emptyList(),
                parseList { User(it) }
            )
        }
    }

    suspend fun addUser(user: User) {
        db.asyncConnection {
            executeUpdate(
                """
                INSERT INTO argent_users (
                    id,
                    name,
                    email,
                    role
                )
                VALUES(?,?,?,?)
                """.trimIndent(),
                listOf(
                    user.id,
                    user.name,
                    user.email,
                    user.role
                )
            )
        }
    }

    suspend fun deleteUser(userId: UUID) {
        db.asyncConnection {
            executeUpdate(
                """
                DELETE FROM argent_users
                WHERE id = ?
                """.trimIndent(),
                listOf(userId)
            )
        }
    }
}
