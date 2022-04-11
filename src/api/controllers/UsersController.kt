package argent.api.controllers

import argent.api.authedHandler
import argent.api.respondOk
import argent.api.serialization.UserForSharing
import argent.api.unAuthedHandler
import argent.data.users.User
import argent.data.users.UserDataStore
import argent.google.getGoogleToken
import argent.jwt.ArgentJwt
import argent.server.ForbiddenException
import argent.server.UnauthorizedException
import argent.server.features.createAuthCookie
import argent.server.features.createExpiredCookie
import argent.util.WithLogger
import argent.util.logger
import io.ktor.http.HttpMethod
import io.ktor.server.application.call
import io.ktor.server.response.respond

class UsersController(private val userDataStore: UserDataStore) : WithLogger {
    val me = authedHandler(HttpMethod.Get) { user ->
        logger.info("getting own user")
        call.respond(user)
    }

    val getAll = authedHandler(HttpMethod.Get) {
        logger.info("getting all users")
        val users = userDataStore.getAllUsers().map { UserForSharing(it) }
        call.respond(users)
    }

    val login = unAuthedHandler(HttpMethod.Get) {
        logger.info("logging in: start")
        val googleToken = call.getGoogleToken()
            ?: throw UnauthorizedException()
        logger.info("logging in: google token valid")
        val user: User = userDataStore.getUserForEmail(googleToken.email)
            ?: throw ForbiddenException()
        logger.info("logging in: user exists in db")
        val argentToken = ArgentJwt.createToken(user)
        call.response.cookies.append(createAuthCookie(argentToken))
        call.respond(user)
    }

    val logout = unAuthedHandler(HttpMethod.Get) {
        call.response.cookies.append(createExpiredCookie())
        call.respondOk()
    }
}
