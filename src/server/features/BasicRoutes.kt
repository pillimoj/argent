package argent.server.features

import argent.api.UtilController
import io.ktor.application.Application
import io.ktor.routing.get
import io.ktor.routing.routing

object BasicRoutes : Feature {
    override val installer: Application.() -> Unit = {
        routing {
            get("/ping", UtilController.ping)
            get("/health-check", UtilController.healthCheck)
        }
    }
}