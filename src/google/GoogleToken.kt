package argent.google

import argent.util.defaultObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpHeaders
import io.ktor.request.header

class GoogleToken(
    val email: String,
)

private fun String.asToken(): GoogleToken {
    return defaultObjectMapper.readValue(this)
}

fun ApplicationCall.getGoogleToken(): GoogleToken? {
    val token = request.header(HttpHeaders.Authorization)?.replace("Bearer ", "")
    val payload = token?.let { GoogleTokenVerification.verify(token) }
    return payload?.asToken()
}
