package argent.api

import java.util.UUID

data class ChecklistReq(val name: String)

data class ChecklistItemReq(
    val checklist: UUID,
    val title: String
)

data class SetItemDoneReq(val done: Boolean)