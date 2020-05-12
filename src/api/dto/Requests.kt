@file:UseSerializers(UUIDSerializer::class)

package argent.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID

@Serializable
data class ChecklistReq(val name: String)

@Serializable
data class ChecklistItemReq(
    val checklist: UUID,
    val title: String
)

@Serializable
data class SetItemDoneReq(val done: Boolean)