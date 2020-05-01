@file:UseSerializers(GMTDateSerializer::class, UUIDSerializer::class)

import com.benasher44.uuid.Uuid
import io.ktor.util.date.GMTDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ChecklistRes(
    val id: Uuid,
    val name: String
)

@Serializable
data class ChecklistWithItemsRes(
    val id: Uuid,
    val name: String,
    val items: List<ChecklistItemRes>
)

@Serializable
data class ChecklistItemRes(
    val id: Uuid,
    val title: String,
    val done: Boolean,
    val createdAt: GMTDate
)

@Serializable
data class ErrorMessage(val error: String){
    companion object
}

@Serializable
object OkResponse {
    val message = "OK"
}