package argent.server

import argent.features.Headers
import argent.features.Logging
import argent.server.features.ApiV1Routes
import argent.server.features.BasicRoutes
import argent.server.features.ErrorHandling
import argent.server.features.GoogleAuthFeature
import argent.server.features.Gzip
import argent.server.features.JsonNegotiation
import argent.server.features.features
import io.ktor.application.Application

fun Application.main() {
    features(
        Logging,
        Headers,
        JsonNegotiation,
        Gzip,
        ErrorHandling,
        GoogleAuthFeature,
        BasicRoutes,
        ApiV1Routes
    )
}
