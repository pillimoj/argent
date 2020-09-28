package argent.server.features

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallId
import io.ktor.features.CallLogging
import io.ktor.features.callIdMdc
import io.ktor.http.HttpHeaders
import io.ktor.request.header
import org.slf4j.event.Level
import java.util.UUID

object Logging: Feature {
    override val installer: Application.() -> Unit = {
        install(CallId) {
            retrieve { call ->
                call.request.header(HttpHeaders.XRequestId)
            }
            retrieve {
                UUID.randomUUID().toString().replace("-", "")
            }
            verify { callId ->
                callId.isNotEmpty()
            }
            replyToHeader(HttpHeaders.XRequestId)
        }
        install(CallLogging) {
            callIdMdc("requestId")
            level = Level.INFO
        }        
    }
}