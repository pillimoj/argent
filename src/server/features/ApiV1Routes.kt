package argent.server.features

import argent.api.v1Routes
import io.ktor.application.Application
import io.ktor.routing.route
import io.ktor.routing.routing

object ApiV1Routes : Feature {
    override val installer: Application.() -> Unit = {
        routing {
            route("api/v1") { v1Routes() }
        }
    }
}