@file:UseSerializers(GMTDateSerializer::class, UUIDSerializer::class)

package argent.data.checklists


import argent.util.GMTDateSerializer
import argent.util.UUIDSerializer
import argent.util.toGMTDate
import com.grimsborn.database.getLocalDateTime
import com.grimsborn.database.getUUID
import io.ktor.util.date.GMTDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.sql.ResultSet
import java.util.UUID

enum class ChecklistAccessType {
    Owner,
    Editor
}

@Serializable
data class Checklist(
    val id: UUID,
    val name: String,
) {
    constructor(rs: ResultSet) : this(
        id = rs.getUUID("id"),
        name = rs.getString("name")
    )
}

@Serializable
data class ChecklistItem(
    val id: UUID,
    val title: String,
    val done: Boolean,
    val checklist: UUID,
    val createdAt: GMTDate,
) {
    constructor(rs: ResultSet) : this(
        id = rs.getUUID("id"),
        title = rs.getString("title"),
        done = rs.getBoolean("done"),
        checklist = rs.getUUID("checklist"),
        createdAt = rs.getLocalDateTime("created_at").toGMTDate()
    )
}
