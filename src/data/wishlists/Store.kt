package argent.data.wishlists

import argent.data.DatabaseQueries
import argent.data.asyncConnection
import argent.data.users.User
import java.util.UUID
import javax.sql.DataSource

class WishlistDataStore(private val db: DataSource) : DatabaseQueries {

    suspend fun getAvailableWishlistsForUser(user: User): List<User> {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT 
                    id,
                    name,
                    email,
                    role
                FROM wishlist_access wa
                LEFT JOIN argent_users u
                    ON u.id = wa.wishlist_user
                WHERE wa.access_user = ?
                """.trimIndent(),
                listOf(user.id),
                parseList { User(it) }
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
                    description
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

    suspend fun getWishlistItem(itemId: UUID): WishlistItem? {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT
                    id,
                    title,
                    description,
                    taken_by,
                    argent_user
                FROM wishlist_items
                WHERE id = ?
                """.trimIndent(),
                listOf(itemId),
                parse { WishlistItem(it) }
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

    suspend fun shareWishlist(wishlistUserId: UUID, shareWithUserId: UUID) {
        db.asyncConnection {
            executeUpdate(
                """
                INSERT INTO wishlist_access (
                    wishlist_user,
                    access_user
                )
                VALUES (?,?)
                """.trimIndent(),
                listOf(wishlistUserId, shareWithUserId)
            )
        }
    }

    suspend fun getUsersWithAccessToWishlist(wishlistUserId: UUID): List<User> {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT 
                    id,
                    name,
                    email,
                    role
                FROM  wishlist_access wa
                LEFT JOIN argent_users u
                    ON u.id = wa.access_user
                WHERE wa.wishlist_user = ?
                """.trimIndent(),
                listOf(wishlistUserId),
                parseList { User(it) }
            )
        }
    }

    suspend fun userHasAccess(wishlistUserId: UUID, user: User): Boolean {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT wishlist_user
                FROM wishlist_access
                WHERE wishlist_user = ?
                AND access_user = ?
                """.trimIndent(),
                listOf(wishlistUserId, user.id),
                parse { }
            )
        } != null
    }
}
