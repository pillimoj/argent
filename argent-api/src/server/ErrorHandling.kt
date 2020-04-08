package argent.server

import argent.util.ApiException
import argent.util.InternalServerError
import argent.util.logger
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respond
import org.slf4j.Logger

private data class ErrorMessage(val error: String) {
    constructor(e: ApiException) : this(e.clientMessage)
}

private fun Logger.apiException(e: ApiException) = when (e) {
    is InternalServerError -> error(e.logMessage, e)
    else -> info(e.logMessage, e)
}

fun Application.installStatusPages() {
    install(StatusPages) {

        exception<ApiException> { cause ->
            logger.apiException(cause)
            call.respond(HttpStatusCode.fromValue(cause.statusCode), ErrorMessage(cause))
        }

        exception<Exception> {
            logger.error("Ktor caught exception", it)
        }

        status(HttpStatusCode.NotFound) {
            if (call.request.path().startsWith("/api")) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorMessage("Not found")
                )
            } else {
                call.respondHtml { renderIndex() }
            }
        }
    }
}
