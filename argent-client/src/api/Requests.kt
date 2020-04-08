package api

data class ChecklistReq(val name: String)

data class ChecklistItemReq(
    val checklist: String,
    val title: String
)

data class SetItemDoneReq(val done: Boolean)