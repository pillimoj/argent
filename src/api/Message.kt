package argent.api

import argent.util.argentJson
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.response.respondText
import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(val error: String)

@Serializable
data class MessageResponse(val message: String)

private val OkResponse = argentJson.encodeToString(MessageResponse.serializer(), MessageResponse("OK"))

suspend fun ApplicationCall.respondOk(){
    respondText(OkResponse, contentType = ContentType.Application.Json)
}