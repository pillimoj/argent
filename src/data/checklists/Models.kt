package argent.data.checklists

import io.ktor.util.date.GMTDate
import java.util.UUID

enum class ChecklistAccessType {
    Owner,
    Editor
}

data class Checklist(
    val id: UUID,
    val name: String,
) {
    companion object
}

data class ChecklistItem(
    val checklistItem: UUID,
    val title: String,
    val done: Boolean,
    val checklist: UUID,
    val createdAt: GMTDate,
) {
    companion object
}
