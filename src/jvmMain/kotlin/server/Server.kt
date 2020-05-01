package argent.server

import argent.api.ApiController
import argent.features.Headers
import argent.features.Logging
import argent.server.features.*
import io.ktor.application.Application

fun Application.mainWithDeps(controller: ApiController) {
    features(
        Logging,
        Headers,
        JsonNegotiation,
        Gzip,
        ErrorHandling,
        IAP
    )
    setUpRoutes(controller)
}
