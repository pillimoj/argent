package argent.jwt

import argent.data.users.User
import argent.server.UnauthorizedException
import argent.util.argentJson
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

private const val MINUTE_MILLIS = 60L * 1000
private const val issuer = "argent"
private fun dateInFutureMinutes(minutesInFuture: Int): Date {
    val result = Date()
    result.time = result.time + minutesInFuture * MINUTE_MILLIS
    return result
}
object ArgentJwt {
    private val algorithm = Algorithm.HMAC256("secret")
    private val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build() // Reusable verifier instance

    fun createToken(user: User): String = JWT.create()
        .withIssuer(issuer)
        .withClaim("argent_user", argentJson.encodeToString(User.serializer(), user))
        .withExpiresAt(dateInFutureMinutes(30))
        .sign(algorithm)

    fun validateToken(token: String): User {
        val validToken = verifier.verify(token) ?: throw UnauthorizedException()
        val userClaim = validToken.getClaim("argent_user").asString()
        return argentJson.decodeFromString(User.serializer(), userClaim)
    }
}
