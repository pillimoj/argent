package argent.google

import argent.server.Config
import argent.util.argentJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant

@Serializable
data class TokenResponse(val access_token: String, val expires_in: Int, val token_type: String)

object RestClient {
    private var token: String? = null
    private var expiration: Instant = Instant.MIN

    fun createClient() = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(argentJson)
        }
    }

    init {
        if(Config.gcpRestTokenOverride != null){
            token = Config.gcpRestTokenOverride
            expiration = Instant.MAX
        }
    }

    suspend fun HttpRequestBuilder.gcpBearerAuth() {
        if (token == null || expiration < Instant.now() - Duration.ofSeconds(30)) {
            val newToken = refreshToken() ?: return
            token = newToken.access_token
            expiration = Instant.now() + Duration.ofMillis(newToken.expires_in.toLong())
        }
        headers["Authorization"]= "Bearer $token"
    }

    private suspend fun refreshToken(): TokenResponse? {
        val client = createClient()
        val result = runCatching {
            client.get("http://metadata.google.internal/computeMetadata/v1/instance/service-accounts/default/token") {
                headers["Metadata-Flavor"] = "Google"
            }
                .body<TokenResponse>()
        }
        client.close()
        return result.getOrNull()
    }
}