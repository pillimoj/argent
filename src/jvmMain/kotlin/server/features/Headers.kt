package argent.features

import argent.server.features.Feature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.features.HSTS
import io.ktor.features.XForwardedHeaderSupport

private val xFrameOptionsHeader = "X-Frame-Options" to "deny"
private val xContentTypeOptions = "X-Content-Type-Options" to "nosniff"
private val referrerPolicyHeader = "Referrer-Policy" to "same-origin"
private val securityHeaders = mapOf(xFrameOptionsHeader, xContentTypeOptions, referrerPolicyHeader)


object Headers: Feature {
    override val installer: Application.() -> Unit = {
        install(XForwardedHeaderSupport)
        install(HSTS)
        install(DefaultHeaders) {
            securityHeaders.forEach { (headerName, headerValue) -> header(headerName, headerValue) }
        }
    }
}

