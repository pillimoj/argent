package argent.server

import argent.api.ApiController
import argent.config.installLoggingFeatures
import argent.iap.IAPUserParsing
import argent.util.addSecurityHeaders
import argent.util.defaultJackson
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson

fun Application.mainWithDeps(controller: ApiController) {

    addSecurityHeaders()
    installLoggingFeatures()
    install(ContentNegotiation) {
        jackson { defaultJackson() }
    }

    installStatusPages()
    install(IAPUserParsing)
    setUpRoutes(controller)
}
