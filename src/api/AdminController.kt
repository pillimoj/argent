package argent.api

import argent.data.users.User
import argent.data.users.UserDataStore
import argent.server.DataBases
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

private val userDataStore = UserDataStore(DataBases.Argent.dbPool)

object AdminController {
    val getAllUsers = adminHandler(HttpMethod.Get){
        val allUsers = userDataStore.getAllUsers()
        call.respond(allUsers)
    }

    val addUser = adminHandler(HttpMethod.Post){
        val newUser = User.deserialize(call)
        userDataStore.addUser(newUser)
        call.respond(OkResponse)
    }
}