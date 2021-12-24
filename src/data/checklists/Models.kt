@file:UseSerializers(GMTDateSerializer::class, UUIDSerializer::class)

package argent.data.checklists

import argent.util.GMTDateSerializer
import argent.util.UUIDSerializer
import argent.util.database.getLocalDateTime
import argent.util.database.getUUID
import argent.util.toGMTDate
import io.ktor.util.date.GMTDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.sql.ResultSet
import java.util.UUID

@Suppress("unused")
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
    companion object
}

@Serializable
data class ChecklistItem(
    val id: UUID,
    val title: String,
    val checklist: UUID,
    val done: Boolean,
    val createdAt: GMTDate,
) {
    constructor(rs: ResultSet) : this(
        id = rs.getUUID("id"),
        title = rs.getString("title"),
        checklist = rs.getUUID("checklist"),
        done = rs.getBoolean("done"),
        createdAt = rs.getLocalDateTime("created_at").toGMTDate()
    )
    companion object
}
