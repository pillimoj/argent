@file:UseSerializers(GMTDateSerializer::class, UUIDSerializer::class)

package argent.data.wishlists

import argent.data.checklists.ChecklistAccessType
import argent.util.GMTDateSerializer
import argent.util.UUIDSerializer
import argent.util.asEnum
import argent.util.getUUID
import argent.util.getUUIDOrNull
import io.ktor.auth.Principal
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.sql.ResultSet
import java.util.UUID


@Serializable
data class WishlistItem(
    val id: UUID,
    val title: String,
    val takenBy: UUID?,
    val user: UUID,
) {
    constructor(rs: ResultSet) : this(
        id = rs.getUUID("id"),
        title = rs.getString("title"),
        takenBy = rs.getUUIDOrNull("taken_by"),
        user = rs.getUUID("argent_user"),
    )
}

@Serializable
data class UserAccess(
    val id: UUID,
    val name: String,
) {
    constructor(rs: ResultSet) : this(
        id = rs.getUUID("id"),
        name = rs.getString("name"),
    )
}
