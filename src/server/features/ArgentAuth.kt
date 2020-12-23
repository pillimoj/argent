package argent.server.features

import argent.jwt.ArgentJwt
import argent.server.ApiException
import argent.server.Config
import argent.server.UnauthorizedException
import argent.util.logger
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.application.call
import io.ktor.auth.Authentication
import io.ktor.auth.AuthenticationPipeline
import io.ktor.auth.AuthenticationProvider
import io.ktor.http.Cookie

fun createAuthCookie(argentToken: String): Cookie{
    val sameSite = "SameSite" to if(Config.authentication.secureCookie) "none" else "strict"
    return Cookie(
        name = Config.authentication.cookieName,
        value = argentToken,
        path = "/api/v1",
        secure = Config.authentication.secureCookie,
        httpOnly = true,
        extensions = mapOf(sameSite)
    )
}

class ArgentAuthProvider internal constructor(
    configuration: Configuration
) : AuthenticationProvider(configuration) {
    // internal val confValue: String = configuration.confValue
    class Configuration internal constructor(name: String?) : AuthenticationProvider.Configuration(name) {
        // var confValue: String = "some valuer"
    }
}

fun Authentication.Configuration.argentAuthJwt(
    name: String? = null,
    configure: ArgentAuthProvider.Configuration.() -> Unit
) {
    val provider = ArgentAuthProvider(ArgentAuthProvider.Configuration(name).apply(configure))
    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        try {
            val argentToken = call.request.cookies[Config.authentication.cookieName] ?: throw UnauthorizedException()
            val userFromToken = ArgentJwt.validateToken(argentToken)
            context.principal(userFromToken)
            return@intercept
        } catch (e: Exception) {
            if (e is ApiException) {
                throw e
            }
            if (e is JWTVerificationException) {
                logger.info("Could not verify argent token", e)
                throw UnauthorizedException()
            }
            logger.error("Token verification error", e)
            throw e
        }
    }
    register(provider)
}