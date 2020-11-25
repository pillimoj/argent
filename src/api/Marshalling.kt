@file:UseSerializers(GMTDateSerializer::class, UUIDSerializer::class)

package argent.api

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
private class ChecklistReq(private val name: String) {
    val value: Checklist get() = Checklist(id = UUID.randomUUID(), name = name)
}

@Serializable
private class ChecklistItemReq(private val title: String, private val checklist: UUID) {
    val value: ChecklistItem
        get() = ChecklistItem(
            id = UUID.randomUUID(),
            title = title,
            checklist = checklist,
            done = false,
            createdAt = GMTDate()
        )
}

suspend fun Checklist.Companion.marshall(call: ApplicationCall) = call.receive<ChecklistReq>().value
suspend fun ChecklistItem.Companion.marshall(call: ApplicationCall) = call.receive<ChecklistItemReq>().value
