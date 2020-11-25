package argent.server.features

import argent.api.ErrorMessage
import argent.server.ApiException
import argent.server.InternalServerError
import argent.util.logger
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respond
import org.slf4j.Logger

fun ApiException.errorMessage() = ErrorMessage(this.clientMessage)

private fun Logger.apiException(e: ApiException) = when (e) {
    is InternalServerError -> error(e.logMessage, e)
    else -> info(e.logMessage, e)
}

object ErrorHandling : Feature {
    override val installer: Application.() -> Unit = {
        install(StatusPages) {

            exception<ApiException> { cause ->
                logger.apiException(cause)
                call.respond(HttpStatusCode.fromValue(cause.statusCode), cause.errorMessage())
            }

            exception<Exception> {
                logger.error("Ktor caught exception", it)
                call.respond(HttpStatusCode.InternalServerError, ErrorMessage("Internal Server Error"))
            }

            status(HttpStatusCode.NotFound) {
                val path = call.request.path()
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorMessage("Not found: $path")
                )
            }
        }
    }
}

