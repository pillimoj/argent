@file:UseSerializers(GMTDateSerializer::class, UUIDSerializer::class)

package argent.api.serialization

import argent.data.checklists.Checklist
import argent.data.checklists.ChecklistItem
import argent.util.GMTDateSerializer
import argent.util.UUIDSerializer
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.util.date.GMTDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID

@Serializable
private class ChecklistReq(val id: UUID?, val name: String) {
    val value: Checklist get() = Checklist(id = id ?: UUID.randomUUID(), name = name)
}

@Serializable
private class ChecklistItemReq(
    val id: UUID?,
    val title: String,
    val checklist: UUID,
    val createdAt: GMTDate?
) {
    val value: ChecklistItem
        get() = ChecklistItem(
            id = id ?: UUID.randomUUID(),
            title = title,
            checklist = checklist,
            done = false,
            createdAt = createdAt ?: GMTDate()
        )
}

suspend fun Checklist.Companion.deserialize(call: ApplicationCall) = call.receive<ChecklistReq>().value
suspend fun ChecklistItem.Companion.deserialize(call: ApplicationCall) = call.receive<ChecklistItemReq>().value
