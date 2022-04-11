package argent.server.features

import argent.jwt.ArgentJwt
import argent.server.ApiException
import argent.server.Config
import argent.server.UnauthorizedException
import argent.util.extra
import argent.util.namedLogger
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.http.Cookie
import io.ktor.server.application.call
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.util.date.GMTDate

fun createAuthCookie(argentToken: String): Cookie {
    val sameSite = "SameSite" to if (Config.authentication.secureCookie) "none" else "strict"
    return Cookie(
        name = Config.authentication.cookieName,
        value = argentToken,
        path = "/api/v1",
        secure = Config.authentication.secureCookie,
        httpOnly = true,
        extensions = mapOf(sameSite)
    )
}

fun createExpiredCookie(): Cookie {
    val sameSite = "SameSite" to if (Config.authentication.secureCookie) "none" else "strict"
    return Cookie(
        name = Config.authentication.cookieName,
        value = "",
        path = "/api/v1",
        secure = Config.authentication.secureCookie,
        httpOnly = true,
        extensions = mapOf(sameSite),
        expires = GMTDate.START
    )
}

class ArgentAuthProvider internal constructor(
    configuration: Configuration,
) : AuthenticationProvider(configuration) {
    // internal val confValue: String = configuration.confValue
    class Configuration internal constructor(name: String?) :
        AuthenticationProvider.Config(name)

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        try {
            val argentToken = context.call.request.cookies[argent.server.Config.authentication.cookieName]
                ?: throw UnauthorizedException()
            val validationResult = ArgentJwt.validateToken(argentToken)
            namedLogger("argent.server.features.ArgentAuth")
                .info("Request Authenticated", extra("user" to validationResult.user.id))
            context.principal(validationResult.user)
        } catch (e: Exception) {
            when (e) {
                is ApiException -> throw e
                is JWTVerificationException -> {
                    namedLogger("argent.server.features.ArgentAuth").info("Could not verify argent token", e)
                    throw UnauthorizedException()
                }
                else -> {
                    namedLogger("argent.server.features.ArgentAuth").error("Token verification error", e)
                    throw e
                }
            }
        }
    }
}

fun AuthenticationConfig.argentAuthJwt(
    name: String? = null,
    configure: ArgentAuthProvider.Configuration.() -> Unit,
) {
    val provider = ArgentAuthProvider(ArgentAuthProvider.Configuration(name).apply(configure))
    register(provider)
}
