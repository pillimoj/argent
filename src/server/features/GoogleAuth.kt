package argent.server.features


import argent.google.TokenVerification
import argent.server.ApiException
import argent.server.Config
import argent.server.ForbiddenException
import argent.server.UnauthorizedException
import argent.util.argentJson
import argent.util.logger
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.AuthenticationPipeline
import io.ktor.auth.AuthenticationProvider
import io.ktor.auth.Principal
import io.ktor.http.HttpHeaders
import io.ktor.request.header
import kotlinx.serialization.Serializable

@Serializable
class User(
    val email: String,
    val given_name: String,
    val family_name: String
): Principal

class GoogleAuthProvider internal constructor(
    configuration: Configuration
) : AuthenticationProvider(configuration) {
    // internal val confValue: String = configuration.confValue
    class Configuration internal constructor(name: String?) : AuthenticationProvider.Configuration(name) {
        // var confValue: String = "some valuer"
    }
}

fun Authentication.Configuration.googleAuthJwt(
    name: String? = null,
    configure: GoogleAuthProvider.Configuration.() -> Unit
) {
    val provider = GoogleAuthProvider(GoogleAuthProvider.Configuration(name).apply(configure))
    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        try {
            val token = call.request.header(HttpHeaders.Authorization)?.replace("Bearer ", "")
            val payload = token?.let { TokenVerification.verify(token) }
            val principal = payload
                ?.let { argentJson.parse(User.serializer(), payload) }
                ?: throw UnauthorizedException()

            if(principal.email !in Config.authenticatedEmails) throw ForbiddenException()

            context.principal(principal)
            return@intercept
        }
        catch (e: Exception){
            if(e is ApiException){
                throw e
            }
            if(e is JWTVerificationException){
                logger.info("Could not verify google token", e)
                throw UnauthorizedException()
            }
            logger.error("Token verification error", e)
            throw e
        }
    }
    register(provider)
}

object GoogleAuthFeature: Feature {
    override val installer: Application.() -> Unit = {
        install(Authentication){
            googleAuthJwt {  }
        }
    }
}