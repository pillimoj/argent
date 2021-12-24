package argent.api.controllers

import argent.api.adminHandler
import argent.api.respondOk
import argent.api.serialization.deserialize
import argent.data.users.User
import argent.data.users.UserDataStore
import argent.util.WithLogger
import argent.util.extra
import argent.util.logger
import argent.util.pathIdParam
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

class AdminController(private val userDataStore: UserDataStore) : WithLogger {
    val getAllUsers = adminHandler(HttpMethod.Get) {
        logger.info("Getting all users")
        val allUsers = userDataStore.getAllUsers()
        call.respond(allUsers)
    }

    val addUser = adminHandler(HttpMethod.Post) {
        val newUser = User.deserialize(call)
        logger.info("Adding user", extra("userId" to newUser.id))
        userDataStore.addUser(newUser)
        call.respondOk()
    }

    val deleteUser = adminHandler(HttpMethod.Delete) {
        val userId = pathIdParam()
        logger.info("deleting user", extra("userId" to userId))
        userDataStore.deleteUser(userId)
        call.respondOk()
    }
}
