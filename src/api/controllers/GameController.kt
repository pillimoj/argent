package argent.api.controllers

import argent.api.authedHandler
import argent.api.respondOk
import argent.api.serialization.GameHighestClearedRequest
import argent.data.game.GameDatastore
import argent.server.ForbiddenException
import argent.util.WithLogger
import argent.util.extra
import argent.util.logger
import io.ktor.http.HttpMethod
import io.ktor.server.application.call
import io.ktor.server.response.respond

class GameController(private val gameDatastore: GameDatastore) : WithLogger {
    val getStatus = authedHandler(HttpMethod.Get) { user ->
        logger.info("getting user game status")
        val gameStatus = gameDatastore.getStatusForUser(user)
        call.respond(gameStatus)
    }

    val setHighestCleared = authedHandler(HttpMethod.Post) { user ->
        logger.info("Setting game highest cleared")
        val req = GameHighestClearedRequest.deserialize(call)
        val currentGameStatus = gameDatastore.getStatusForUser(user)
        val levelIncrease = req.highestCleared - currentGameStatus.highestCleared
        logger.info("level increase for highest cleared", extra("levelIncrease" to levelIncrease))
        when {
            levelIncrease == 1 -> gameDatastore.setHighestClearedForUser(user, req.highestCleared)
            levelIncrease > 1 -> throw ForbiddenException()
            else -> Unit
        }
        call.respondOk()
    }
}
