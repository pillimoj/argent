package argent.server.features

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.plugins.cors.CORSConfig

fun CORSConfig.configureCORS() {
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Patch)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowHeader("X-Requested-With")
    allowHeader(HttpHeaders.XForwardedProto)
    allowHeader("X-Request-ID")
    allowHeader(HttpHeaders.Authorization)
    allowHost("localhost:5000")
    allowHost("argent.grimsborn.com", schemes = listOf("https"))
    allowCredentials = true
    allowNonSimpleContentTypes = true
    exposeHeader("X-Request-ID")
}
