package api


data class User(
    val email: String,
    val id: String
)


data class ChecklistRes(
    val id: String,
    val name: String
)

data class ChecklistWithItemsRes(
    val id: String,
    val name: String,
    val items: Array<ChecklistItemRes>
)

data class ChecklistItemRes(
    val id: String,
    val title: String,
    val done: Boolean,
    val createdAt: String
)
