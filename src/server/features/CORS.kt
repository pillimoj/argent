package argent.server.features

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod

fun Application.installCORS() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Post)
        method(HttpMethod.Patch)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        header("X-Requested-With")
        header(HttpHeaders.XForwardedProto)
        header("X-Request-ID")
        header(HttpHeaders.Authorization)
        host("localhost:5000")
        host("argent.grimsborn.com", schemes = listOf("https"))
        allowCredentials = true
        allowNonSimpleContentTypes = true
        exposeHeader("X-Request-ID")
    }
}
