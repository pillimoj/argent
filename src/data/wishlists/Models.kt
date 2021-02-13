@file:UseSerializers(GMTDateSerializer::class, UUIDSerializer::class)

package argent.data.wishlists

import argent.util.GMTDateSerializer
import argent.util.UUIDSerializer
import com.grimsborn.database.getUUID
import com.grimsborn.database.getUUIDOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.sql.ResultSet
import java.util.UUID

@Serializable
data class WishlistItem(
    val id: UUID,
    val title: String,
    val description: String,
    val takenBy: UUID?,
    val user: UUID,
) {
    constructor(rs: ResultSet) : this(
        id = rs.getUUID("id"),
        title = rs.getString("title"),
        description = rs.getString("description"),
        takenBy = rs.getUUIDOrNull("taken_by"),
        user = rs.getUUID("argent_user"),
    )
}
