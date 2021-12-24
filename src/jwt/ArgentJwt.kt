package argent.jwt

import argent.data.users.User
import argent.server.Config
import argent.server.UnauthorizedException
import argent.util.argentJson
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.Date

data class TokenValidationResult(val token: DecodedJWT, val user: User)

private const val MINUTE_MILLIS = 60L * 1000
private const val issuer = "argent"
private fun dateInFutureMinutes(minutesInFuture: Int): Date {
    val result = Date()
    result.time = result.time + minutesInFuture * MINUTE_MILLIS
    return result
}
object ArgentJwt {
    private val algorithm = Algorithm.HMAC256(Config.authentication.jwtKey)
    private val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build() // Reusable verifier instance

    fun createToken(user: User): String = JWT.create()
        .withIssuer(issuer)
        .withClaim("argent_user", argentJson.encodeToString(User.serializer(), user))
        .withExpiresAt(dateInFutureMinutes(30))
        .sign(algorithm)

    fun validateToken(token: String): TokenValidationResult {
        val validToken = verifier.verify(token) ?: throw UnauthorizedException()
        val userClaim = validToken.getClaim("argent_user").asString()
        val user = argentJson.decodeFromString(User.serializer(), userClaim)
        return TokenValidationResult(validToken, user)
    }
}
