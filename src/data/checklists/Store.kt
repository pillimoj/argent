package argent.data.checklists

import argent.data.users.User
import argent.util.asEnum
import argent.util.toLocalDateTime
import com.grimsborn.database.DatabaseQueries
import com.grimsborn.database.asyncConnection
import java.util.UUID
import javax.sql.DataSource

class ChecklistDataStore(private val db: DataSource) : DatabaseQueries {

    suspend fun getChecklistsForUser(user: User): List<Checklist> {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT id, name
                FROM checklists c
                LEFT JOIN checklist_access ca
                ON c.id = ca.checklist
                WHERE ca.argent_user = ?
                """.trimIndent(),
                listOf(user.id),
                parseList { Checklist(it) }
            )
        }
    }

    suspend fun getChecklist(checklistId: UUID): Checklist? {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT id, name
                FROM checklists
                WHERE id = ?
                """.trimIndent(),
                listOf(checklistId),
                parse { Checklist(it) }
            )
        }
    }

    suspend fun getChecklistItems(checklistId: UUID): List<ChecklistItem> {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT id, title, done, created_at, checklist
                FROM checklistitems
                WHERE checklist = ?
                ORDER BY created_at
                """.trimIndent(),
                listOf(checklistId),
                parseList { ChecklistItem(it) }
            )
        }
    }

    suspend fun addChecklist(checklist: Checklist, user: User) {
        db.asyncConnection {
            transaction {
                executeUpdate(
                    """
               INSERT INTO checklists (
                    id,
                    name
               )
               VALUES(?,?)
                    """.trimIndent(),
                    listOf(checklist.id, checklist.name)
                )
                executeUpdate(
                    """
                INSERT INTO checklist_access (
                    checklist,
                    argent_user,
                    access_type
                )
                VALUES(?,?,?)
                    """.trimIndent(),
                    listOf(
                        checklist.id,
                        user.id,
                        ChecklistAccessType.Owner
                    )
                )
            }
        }
    }

    suspend fun getItem(itemId: UUID): ChecklistItem? {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT
                    id,
                    title,
                    done,
                    created_at,
                    checklist
                FROM checklistitems
                WHERE id = ?
                """.trimIndent(),
                listOf(itemId),
                parse { ChecklistItem(it) }
            )
        }
    }

    suspend fun addItem(checklistItem: ChecklistItem) {
        return db.asyncConnection {
            executeUpdate(
                """
                INSERT INTO checklistitems (
                    id,
                    title,
                    done,
                    checklist,
                    created_at
                )
                VALUES (?,?,?,?,?)
                """.trimIndent(),
                listOf(
                    checklistItem.id,
                    checklistItem.title,
                    checklistItem.done,
                    checklistItem.checklist,
                    checklistItem.createdAt.toLocalDateTime(),
                )
            )
        }
    }

    suspend fun setItemDone(checklistItemId: UUID, isDone: Boolean) {
        db.asyncConnection {
            executeUpdate(
                """
                UPDATE checklistitems
                SET done = ?
                WHERE id = ?
                """.trimIndent(),
                listOf(isDone, checklistItemId)
            )
        }
    }

    suspend fun deleteChecklist(checklistId: UUID) {
        db.asyncConnection {
            transaction {
                executeUpdate(
                    """
                    DELETE FROM checklistitems
                    WHERE checklist = ?
                    """.trimIndent(),
                    listOf(checklistId)
                )
                executeUpdate(
                    """
                    DELETE FROM checklists
                    WHERE id = ?
                    """.trimIndent(),
                    listOf(checklistId)
                )
            }
        }
    }

    suspend fun clearDone(checklistId: UUID) {
        db.asyncConnection {
            executeUpdate(
                """
                DELETE FROM checklistitems
                WHERE checklist = ?
                AND done
                """.trimIndent(),
                listOf(checklistId)
            )
        }
    }

    suspend fun getAccessType(checklistId: UUID, user: User): ChecklistAccessType? {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT access_type
                FROM checklist_access
                WHERE checklist = ?
                AND argent_user = ?
                """.trimIndent(),
                listOf(
                    checklistId,
                    user.id,
                ),
                parse { it.getString("access_type").asEnum<ChecklistAccessType>() },
            )
        }
    }

    suspend fun addUserAccess(checklistId: UUID, userId: UUID, accessType: ChecklistAccessType) {
        db.asyncConnection {
            executeUpdate(
                """
                INSERT INTO checklist_access (
                    checklist,
                    argent_user,
                    access_type
                )
                VALUES (?,?,?)
                """.trimIndent(),
                listOf(
                    checklistId,
                    userId,
                    accessType,
                )
            )
        }
    }

    suspend fun removeUserAccess(checklistId: UUID, userId: UUID) {
        db.asyncConnection {
            executeUpdate(
                """
                DELETE FROM checklist_access
                WHERE checklist = ?
                AND argent_user = ?
                """.trimIndent(),
                listOf(
                    checklistId,
                    userId,
                )
            )
        }
    }
}
