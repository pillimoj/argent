package argent.server.features

import argent.data.users.User
import argent.data.users.UserDataStore
import argent.google.TokenVerification
import argent.google.getGoogleToken
import argent.server.ApiException
import argent.server.DataBases
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
import io.ktor.http.HttpHeaders
import io.ktor.request.header
import kotlinx.serialization.Serializable

class GoogleAuthProvider internal constructor(
    configuration: Configuration
) : AuthenticationProvider(configuration) {
    // internal val confValue: String = configuration.confValue
    internal val usersStore = UserDataStore(DataBases.Argent.dbPool)

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
            val googleToken = call.getGoogleToken() ?: throw UnauthorizedException()

            val user: User = provider.usersStore.getUserForEmail(googleToken.email)
                ?: throw ForbiddenException()
            context.principal(user)
            return@intercept
        } catch (e: Exception) {
            if (e is ApiException) {
                throw e
            }
            if (e is JWTVerificationException) {
                logger.info("Could not verify google token", e)
                throw UnauthorizedException()
            }
            logger.error("Token verification error", e)
            throw e
        }
    }
    register(provider)
}

object GoogleAuthFeature : Feature {
    override val installer: Application.() -> Unit = {
        install(Authentication) {
            googleAuthJwt { }
        }
    }
}