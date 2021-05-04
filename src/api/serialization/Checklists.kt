
package argent.api.serialization

import argent.data.checklists.Checklist
import argent.data.checklists.ChecklistItem
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.util.date.GMTDate
import java.util.UUID

private class ChecklistReq(val id: UUID? = null, val name: String) {
    val value: Checklist get() = Checklist(id = id ?: UUID.randomUUID(), name = name)
}

private class ChecklistItemReq(
    val id: UUID? = null,
    val title: String,
    val checklist: UUID,
    val createdAt: GMTDate? = null
) {
    val value: ChecklistItem
        get() = ChecklistItem(
            checklistItem = id ?: UUID.randomUUID(),
            title = title,
            checklist = checklist,
            done = false,
            createdAt = createdAt ?: GMTDate()
        )
}

suspend fun Checklist.Companion.deserialize(call: ApplicationCall) = call.receive<ChecklistReq>().value
suspend fun ChecklistItem.Companion.deserialize(call: ApplicationCall) = call.receive<ChecklistItemReq>().value
