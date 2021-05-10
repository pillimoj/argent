package argent.google

import argent.util.WithLogger
import argent.util.logger
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.net.URL
import java.security.interfaces.RSAPublicKey
import java.util.Base64
import java.util.concurrent.TimeUnit

fun base64Decode(encoded: String): String = String(Base64.getDecoder().decode(encoded))

object GoogleTokenVerification : WithLogger {
    private val provider: JwkProvider = JwkProviderBuilder(URL("https://www.googleapis.com/oauth2/v3/certs"))
        .cached(5, 5, TimeUnit.HOURS)
        .build()

    fun verify(token: String): String? = try {
        val jwt = JWT.decode(token)
        val jwk = provider[jwt.keyId]
        val algorithm: Algorithm = Algorithm.RSA256(jwk.publicKey as RSAPublicKey, null)
        algorithm.verify(jwt)
        base64Decode(jwt.payload)
    } catch (e: Exception) {
        logger.warn("Token verification failed", e)
        null
    }
}
