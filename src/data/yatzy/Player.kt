@file:UseSerializers(UUIDSerializer::class)

package argent.data.yatzy

import argent.data.users.User
import argent.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID

@Serializable
class Player(val name: String, val id: UUID, val column: YatzyColumn) {
    companion object {
        fun create(user: User): Player {
            return Player(user.name, user.id, YatzyColumn.create())
        }
    }
}