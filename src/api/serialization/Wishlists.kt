@file:UseSerializers(UUIDSerializer::class)

package argent.api.serialization

import argent.data.wishlists.WishlistItem
import argent.util.UUIDSerializer
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID

@Serializable
private class WishlistItemReq(val id: UUID?, val title: String, val description: String, val user: UUID) {
    val value: WishlistItem
        get() = WishlistItem(
            id = id ?: UUID.randomUUID(),
            title = title,
            description = description,
            takenBy = null,
            user = user
        )
}

@Serializable
class WishlistShareRequest(val user: UUID) {
    companion object {
        suspend fun deserialize(call: ApplicationCall) = call.receive<WishlistShareRequest>()
    }
}

suspend fun WishlistItem.Companion.deserialize(call: ApplicationCall) = call.receive<WishlistItemReq>().value
