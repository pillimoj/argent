package argent.api.controllers

import argent.api.unAuthedHandler
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

object UtilController {
    val ping = unAuthedHandler(HttpMethod.Get) {
        call.respond("Pong")
    }

    val healthCheck = unAuthedHandler(HttpMethod.Get) {
        call.respond("OK")
    }
}
