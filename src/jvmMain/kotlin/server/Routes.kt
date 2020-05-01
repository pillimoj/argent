package argent.server

import argent.api.ApiController
import argent.api.UtilController
import argent.api.apiRoutes
import io.ktor.application.Application
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.setUpRoutes(controller: ApiController) {
    routing {
        get("/ping", UtilController.ping)
        get("/health-check", UtilController.healthCheck)

        route("api") { apiRoutes(controller) }
        get("/", indexHandler)
        static("/") {
            resources("public")
        }
    }
}
