package argent.util

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.features.HSTS
import io.ktor.features.XForwardedHeaderSupport
import io.ktor.util.AttributeKey

val xFrameOptionsHeader = "X-Frame-Options" to "deny"
val xContentTypeOptions = "X-Content-Type-Options" to "nosniff"
val referrerPolicyHeader = "Referrer-Policy" to "same-origin"
val securityHeaders = mapOf(xFrameOptionsHeader, xContentTypeOptions, referrerPolicyHeader)

fun Application.addSecurityHeaders() {
    install(XForwardedHeaderSupport)
    install(HSTS)
    install(DefaultHeaders) {
        securityHeaders.forEach { (headerName, headerValue) -> header(headerName, headerValue) }
    }
    // install(CSPHeader)
}

val NonceAttributeKey = AttributeKey<String>("csp-nonce")
//
// class CSPHeader(configuration: Configuration) {
//     class Configuration
//
//     private val cspDirectives = { nonce: String ->
//         listOf(
//             "default-src 'none'",
//             "script-src 'self'",
//             "object-src 'none'",
//             "style-src 'self'",
//             "img-src 'self'",
//             "media-src 'none'",
//             "frame-src 'none'",
//             "font-src 'none'",
//             "connect-src 'none'"
//         )
//     }
//     private val headerName = "Content-Security-Policy"
//
//     // Body of the feature
//     private fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
//         // Add csp header to the response
//         val nonce = makeNonce()
//         context.call.attributes.put(NonceAttributeKey, nonce)
//         val headerValue = cspDirectives(nonce).joinToString(";")
//         context.call.response.header(headerName, headerValue)
//     }
//
//     companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, CSPHeader> {
//         override val key = AttributeKey<CSPHeader>("CustomHeader")
//         override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CSPHeader {
//             // Call user code to configure a feature
//             val configuration = Configuration().apply(configure)
//
//             // Create a feature instance
//             val feature = CSPHeader(configuration)
//
//             // Install an interceptor that will be run on each call and call feature instance
//             pipeline.intercept(ApplicationCallPipeline.Call) {
//                 feature.intercept(this)
//             }
//
//             // Return a feature instance so that client code can use it
//             return feature
//         }
//     }
// }
