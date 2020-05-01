package argent.server.features

import ErrorMessage
import argent.server.ApiException
import argent.server.InternalServerError
import argent.server.renderIndex
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
}

