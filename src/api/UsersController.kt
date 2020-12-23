package argent.api

import argent.data.users.User
import argent.data.users.UserDataStore
import argent.google.getGoogleToken
import argent.jwt.ArgentJwt
import argent.server.ForbiddenException
import argent.server.UnauthorizedException
import argent.server.features.createAuthCookie
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

class UsersController(private val userDataStore: UserDataStore) {
    val getAll = authedHandler(HttpMethod.Get){
        val users = userDataStore.getAllUsers().map { UserForSharing(it) }
        call.respond(users)
    }

    val login = unAuthedHandler(HttpMethod.Get){
        val googleToken = call.getGoogleToken()
            ?: throw UnauthorizedException()
        val user: User = userDataStore.getUserForEmail(googleToken.email)
            ?: throw ForbiddenException()
        val argentToken = ArgentJwt.createToken(user)
        call.response.cookies.append(createAuthCookie(argentToken))
        call.respond(user)
    }
}