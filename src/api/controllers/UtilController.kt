package argent.api.controllers

import argent.api.unAuthedHandler
import io.ktor.http.HttpMethod
import io.ktor.server.application.call
import io.ktor.server.response.respond

object UtilController {
    val ping = unAuthedHandler(HttpMethod.Get) {
        call.respond("Pong")
    }

    val healthCheck = unAuthedHandler(HttpMethod.Get) {
        call.respond("OK")
    }
}
