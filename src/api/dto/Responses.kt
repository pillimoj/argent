@file:UseSerializers(GMTDateSerializer::class, UUIDSerializer::class)

package argent.api.dto

import io.ktor.util.date.GMTDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID

@Serializable
data class ChecklistRes(
    val id: UUID,
    val name: String
)

@Serializable
data class ChecklistWithItemsRes(
    val id: UUID,
    val name: String,
    val items: List<ChecklistItemRes>
)

@Serializable
data class ChecklistItemRes(
    val id: UUID,
    val title: String,
    val done: Boolean,
    val createdAt: GMTDate
)

@Serializable
data class ErrorMessage(val error: String)

@Serializable
object OkResponse {
    val message = "OK"
}