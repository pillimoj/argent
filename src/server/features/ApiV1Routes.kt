package argent.server.features

import argent.ArgentDependencies
import argent.api.apiRoutes
import io.ktor.application.Application
import io.ktor.routing.route
import io.ktor.routing.routing

object ApiV1Routes : Feature {
    override val installer: Application.() -> Unit = {
        routing {
            route("api/v1") { apiRoutes(ArgentDependencies.controller) }
        }
    }
}