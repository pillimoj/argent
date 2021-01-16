package argent.data.wishlists

import argent.api.serialization.UserForSharing
import argent.data.users.User
import argent.server.asyncConnection
import argent.util.DatabaseQueries
import java.util.UUID
import javax.sql.DataSource

class WishlistDataStore(private val db: DataSource) : DatabaseQueries {

    suspend fun getAvailableWishlistsForUser(user: User): List<UserForSharing> {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT id, name, email, role
                FROM argent_users u
                LEFT JOIN wishlist_access wa
                    ON u.id = wa.wishlist_user
                WHERE wa.access_user = ?
                """.trimIndent(),
                listOf(user.id),
                parseList { UserForSharing(User(it)) }
            )
        }
    }

    suspend fun getWishesForUser(userId: UUID): List<WishlistItem> {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT
                    id,
                    title,
                    taken_by,
                    argent_user
                FROM wishlist_items
                WHERE argent_user = ?
                """.trimIndent(),
                listOf(userId),
                parseList { WishlistItem(it) }
            )
        }
    }

    suspend fun addItem(wishlistItem: WishlistItem) {
        return db.asyncConnection {
            executeUpdate(
                """
                INSERT INTO wishlist_items (
                    id,
                    title,
                    taken_by,
                    argent_user
                )
                VALUES (?,?,?,?)
                """.trimIndent(),
                listOf(
                    wishlistItem.id,
                    wishlistItem.title,
                    wishlistItem.takenBy,
                    wishlistItem.user
                )
            )
        }
    }

    suspend fun setItemTaken(wishlistItemId: UUID, takenBy: User) {
        db.asyncConnection {
            executeUpdate(
                """
                UPDATE wishlist_items
                SET taken_by = ?
                WHERE id = ?
                """.trimIndent(),
                listOf(takenBy.id, wishlistItemId)
            )
        }
    }

    suspend fun deleteItem(wishlistItemId: UUID) {
        db.asyncConnection {
            executeUpdate(
                """
                DELETE FROM wishlist_items
                WHERE id = ?
                """.trimIndent(),
                listOf(wishlistItemId)
            )
        }
    }
}
