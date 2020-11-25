package argent.api

import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

object UtilController {
    val ping = handler(HttpMethod.Get) {
        call.respond("Pong")
    }

    val healthCheck = handler(HttpMethod.Get) {
        call.respond("OK")
    }
}
