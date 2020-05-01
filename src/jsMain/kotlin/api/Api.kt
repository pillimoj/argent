package api

import ChecklistRes
import ChecklistWithItemsRes
import IAPUser
import SetItemDoneReq
import com.benasher44.uuid.Uuid
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType


suspend fun getUser() = JsonClient.get<IAPUser>("$endpoint/api/user/")
suspend fun getLists() = JsonClient.get<Array<ChecklistRes>>("$endpoint/api/checklists/")
suspend fun getList(id: Uuid) = JsonClient.get<ChecklistWithItemsRes>("$endpoint/api/checklists/$id/")
suspend fun setItemDone(id: Uuid, done: Boolean) = JsonClient.post<Unit>("$endpoint/api/checklistitems/$id/done/"){
    contentType(ContentType.Application.Json)
    body = SetItemDoneReq(done)
}
