package argent.api.serialization

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import kotlinx.serialization.Serializable

@Serializable
class GameHighestClearedRequest(val highestCleared: Int) {
    companion object {
        suspend fun deserialize(call: ApplicationCall) = call.receive<GameHighestClearedRequest>()
    }
}
