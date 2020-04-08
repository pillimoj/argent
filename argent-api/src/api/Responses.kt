package argent.api

import argent.checklists.Checklist
import argent.checklists.ChecklistItem
import java.time.LocalDateTime
import java.util.UUID

data class ChecklistRes(
    val id: UUID,
    val name: String
) {
    constructor(dao: Checklist) : this(
        dao.id.value,
        dao.name
    )
}

data class ChecklistWithItemsRes(
    val id: UUID,
    val name: String,
    val items: List<ChecklistItemRes>
)

data class ChecklistItemRes(
    val id: UUID,
    val title: String,
    val done: Boolean,
    val createdAt: LocalDateTime
) {
    constructor(dao: ChecklistItem) : this(
        dao.id.value,
        dao.title,
        dao.done,
        dao.createdAt
    )
}

