package argent.api

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(val error: String)

@Serializable
data class MessageResponse(val message: String)

val OkResponse = MessageResponse("OK")
