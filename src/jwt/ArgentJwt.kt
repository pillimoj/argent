package argent.jwt

import argent.data.users.User
import argent.server.UnauthorizedException
import argent.util.argentJson
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.impl.NullClaim
import kotlinx.serialization.decodeFromString

private val issuer = "argent"
object ArgentJwt {
    private val algorithm = Algorithm.HMAC256("secret")
    private val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build() //Reusable verifier instance

    fun createToken(user: User): String = JWT.create()
            .withIssuer(issuer)
            .withClaim("argent_user", argentJson.encodeToString(User.serializer(), user))
            .sign(algorithm)

    fun validateToken(token: String): User {
        val validToken = verifier.verify(token) ?: throw UnauthorizedException()
        val userClaim = validToken.getClaim("argent_user").asString()
        return argentJson.decodeFromString(User.serializer(), userClaim)
    }
}