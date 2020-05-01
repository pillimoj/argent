package argent.server.features

import IAPUser
import io.ktor.application.*
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext

val IAPUserKey = AttributeKey<IAPUser>("iap-user")

object IAP: Feature {
    override val installer: Application.() -> Unit = {
        install(IAPUserParsing)
    }
}

private class IAPUserParsing(configuration: Configuration) {
    class Configuration

    private val emailHeaderName = "X-Goog-Authenticated-User-Email"
    private val idHeaderName = "X-Goog-Authenticated-User-Email"

    // Body of the feature
    private fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        // Add csp header to the response
        val email = context.call.request.headers[emailHeaderName]?.replace("accounts.google.com:", "")
        val id = context.call.request.headers[idHeaderName]
        val user = IAPUser(
            email ?: "developer@localhost",
            id ?: "google-id"
        )
        context.call.attributes.put(IAPUserKey, user)
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, IAPUserParsing> {
        override val key = AttributeKey<IAPUserParsing>("iap-user-parsing")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): IAPUserParsing {
            // Call user code to configure a feature
            val configuration = Configuration().apply(configure)

            // Create a feature instance
            val feature = IAPUserParsing(configuration)

            // Install an interceptor that will be run on each call and call feature instance
            pipeline.intercept(ApplicationCallPipeline.Call) {
                feature.intercept(this)
            }

            // Return a feature instance so that client code can use it
            return feature
        }
    }
}

