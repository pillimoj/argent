package argent.features

import argent.server.features.Feature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.HSTS
import io.ktor.features.XForwardedHeaderSupport


object Headers: Feature {
    override val installer: Application.() -> Unit = {
        install(XForwardedHeaderSupport)
        install(HSTS)
    }
}

