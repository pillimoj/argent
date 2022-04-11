package argent.google

import argent.util.argentJson
import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import kotlinx.serialization.Serializable

@Serializable
class GoogleToken(
    val email: String,
)

private fun String.asToken(): GoogleToken {
    return argentJson.decodeFromString(GoogleToken.serializer(), this)
}

fun ApplicationCall.getGoogleToken(): GoogleToken? {
    val token = request.header(HttpHeaders.Authorization)?.replace("Bearer ", "")
    val payload = token?.let { GoogleTokenVerification.verify(token) }
    return payload?.asToken()
}
