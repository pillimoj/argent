package api


suspend fun getUser() = request<User>("/api/user/")
suspend fun getLists() = request<Array<ChecklistRes>>("/api/checklists")
suspend fun getList(id: String) = request<ChecklistWithItemsRes>("/api/checklists/$id")
suspend fun setItemDone(id: String, done: Boolean) = request<Unit>("/api/checklistitems/$id/done"){
    method = Method.POST
    body = SetItemDoneReq(done)
}
