package argent.api.serialization

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import kotlinx.serialization.Serializable

@Serializable
class GameHighestClearedRequest(val highestCleared: Int) {
    companion object {
        suspend fun deserialize(call: ApplicationCall) = call.receive<GameHighestClearedRequest>()
    }
}
