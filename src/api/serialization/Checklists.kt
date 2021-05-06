
package argent.api.serialization

import argent.data.checklists.Checklist
import argent.data.checklists.ChecklistItem
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import java.time.Instant
import java.util.UUID

private class ChecklistReq(val id: UUID? = null, val name: String) {
    val value: Checklist get() = Checklist(checklist = id ?: UUID.randomUUID(), name = name)
}

private class ChecklistItemReq(
    val id: UUID? = null,
    val title: String,
    val createdAt: Instant? = null
) {
    val value: ChecklistItem
        get() = ChecklistItem(
            checklistItem = id ?: UUID.randomUUID(),
            title = title,
            done = false,
            createdAt = createdAt ?: Instant.now()
        )
}

suspend fun Checklist.Companion.deserialize(call: ApplicationCall) = call.receive<ChecklistReq>().value
suspend fun ChecklistItem.Companion.deserialize(call: ApplicationCall) = call.receive<ChecklistItemReq>().value
