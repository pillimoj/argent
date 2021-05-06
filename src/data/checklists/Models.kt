package argent.data.checklists

import java.time.Instant
import java.util.UUID

enum class ChecklistAccessType {
    Owner,
    Editor
}

data class Checklist(
    val checklist: UUID,
    val name: String,
) {
    companion object
}

data class ChecklistItem(
    val checklistItem: UUID,
    val title: String,
    val done: Boolean,
    val createdAt: Instant,
) {
    companion object
}
