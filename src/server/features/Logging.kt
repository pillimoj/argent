package argent.server.features

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.header
import org.slf4j.event.Level
import java.util.UUID

fun Application.installCallLogging() {
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
