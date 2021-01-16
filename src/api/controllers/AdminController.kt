package argent.api.controllers

import argent.api.adminHandler
import argent.api.respondOk
import argent.api.serialization.deserialize
import argent.data.users.User
import argent.data.users.UserDataStore
import argent.util.pathIdParam
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

class AdminController(private val userDataStore: UserDataStore) {
    val getAllUsers = adminHandler(HttpMethod.Get) {
        val allUsers = userDataStore.getAllUsers()
        call.respond(allUsers)
    }

    val addUser = adminHandler(HttpMethod.Post) {
        val newUser = User.deserialize(call)
        userDataStore.addUser(newUser)
        call.respondOk()
    }

    val deleteUser = adminHandler(HttpMethod.Delete) {
        val userId = pathIdParam()
        userDataStore.deleteUser(userId)
        call.respondOk()
    }
}
