package argent.server

import argent.server.features.Headers
import argent.server.features.Logging
import argent.server.features.ApiV1Routes
import argent.server.features.ArgentAuthFeature
import argent.server.features.BasicRoutes
import argent.server.features.CORS
import argent.server.features.ErrorHandling
import argent.server.features.Feature
import argent.server.features.Gzip
import argent.server.features.JsonNegotiation
import argent.server.features.features
import io.ktor.application.Application

fun Application.main(){
    // no overrides
    mainWithOverrides()
}

fun Application.mainWithOverrides(authenticationFeature: Feature = ArgentAuthFeature) {
    features(
        Logging,
        Headers,
        JsonNegotiation,
        Gzip,
        CORS,
        ErrorHandling,
        authenticationFeature,
        BasicRoutes,
        ApiV1Routes
    )
}
