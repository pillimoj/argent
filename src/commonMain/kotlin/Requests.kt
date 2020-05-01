@file:UseSerializers(UUIDSerializer::class)


import com.benasher44.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ChecklistReq(val name: String)

@Serializable
data class ChecklistItemReq(
    val checklist: Uuid,
    val title: String
)

@Serializable
data class SetItemDoneReq(val done: Boolean)