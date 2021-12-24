@file:UseSerializers(UUIDSerializer::class)

package argent.data.game

import argent.util.UUIDSerializer
import argent.util.database.getUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.sql.ResultSet
import java.util.UUID

@Serializable
data class GameStatus(val user: UUID, val highestCleared: Int) {
    constructor(rs: ResultSet) : this(
        rs.getUUID("argent_user"),
        rs.getInt("highest_cleared")
    )
}
