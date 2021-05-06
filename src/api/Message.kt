package argent.api

import argent.util.defaultObjectMapper
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.response.respondText

data class ErrorMessage(val error: String)

data class MessageResponse(val message: String)

private val OkResponse = defaultObjectMapper.writeValueAsString(MessageResponse("OK"))

suspend fun ApplicationCall.respondOk() {
    respondText(OkResponse, contentType = ContentType.Application.Json)
}
