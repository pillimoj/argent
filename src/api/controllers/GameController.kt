package argent.api.controllers

import argent.api.authedHandler
import argent.api.respondOk
import argent.api.serialization.GameHighestClearedRequest
import argent.data.game.GameDatastore
import argent.data.game.GameStatus
import argent.server.ForbiddenException
import argent.server.InternalServerError
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

class GameController(private val gameDatastore: GameDatastore) {
    val getStatus = authedHandler(HttpMethod.Get) { user ->
        val gameStatus = gameDatastore.getStatusForUser(user)
        val defaultGameStatus = GameStatus(user.id, 0)
        if (gameStatus == null) {
            gameDatastore.createGameStatus(defaultGameStatus)
        }
        call.respond(gameStatus ?: defaultGameStatus)
    }

    val setHighestCleared = authedHandler(HttpMethod.Post) { user ->
        val req = GameHighestClearedRequest.deserialize(call)
        val currentGameStatus = gameDatastore.getStatusForUser(user)
            ?: throw InternalServerError("Tried to set high score for user without playing")
        val levelIncrease = req.highestCleared - currentGameStatus.highestCleared
        when {
            levelIncrease == 1 -> gameDatastore.setHighestClearedForUser(user, req.highestCleared)
            levelIncrease > 1 -> throw ForbiddenException()
            else -> Unit
        }
        call.respondOk()
    }
}
