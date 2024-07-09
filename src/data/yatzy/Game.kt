@file:UseSerializers(UUIDSerializer::class)

package argent.data.yatzy

import argent.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID

@Serializable
class Game(val players: List<Player>, var currentPlayer: UUID) {

}