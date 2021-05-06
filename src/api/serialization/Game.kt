package argent.api.serialization

import io.ktor.application.ApplicationCall
import io.ktor.request.receive

class GameHighestClearedRequest(val highestCleared: Int) {
    companion object {
        suspend fun deserialize(call: ApplicationCall) = call.receive<GameHighestClearedRequest>()
    }
}
