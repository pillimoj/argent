package argent.server.features

import argent.api.ErrorMessage
import argent.server.ApiException
import argent.server.InternalServerError
import argent.util.namedLogger
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.path
import io.ktor.server.response.respond
import org.slf4j.Logger

fun ApiException.errorMessage() = ErrorMessage(this.clientMessage)

private fun Logger.apiException(e: ApiException) = when (e) {
    is InternalServerError -> error(e.logMessage, e)
    else -> info(e.logMessage, e)
}

fun StatusPagesConfig.configureStatusPages() {
    exception<ApiException> { call, cause ->
        namedLogger("argent.server.features.ErrorHandling").apiException(cause)
        call.respond(HttpStatusCode.fromValue(cause.statusCode), cause.errorMessage())
    }

    exception<Exception> { call, cause ->
        namedLogger("argent.server.features.ErrorHandling").error("Ktor caught exception", cause)
        call.respond(HttpStatusCode.InternalServerError, ErrorMessage("Internal Server Error"))
    }

    status(HttpStatusCode.NotFound) { call, _ ->
        val path = call.request.path()
        call.respond(
            HttpStatusCode.NotFound,
            ErrorMessage("Not found: $path")
        )
    }
}
