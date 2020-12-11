package argent.api

import argent.data.checklists.ChecklistDataStore
import argent.data.users.UserDataStore
import argent.google.getGoogleToken
import argent.server.DataBases
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond

private val checklistDataStore = ChecklistDataStore(DataBases.Argent.dbPool)
private val userDataStore = UserDataStore(DataBases.Argent.dbPool)

object AdminController {
    val getAllUsers = adminHandler(HttpMethod.Get){
        val allUsers = userDataStore.getAllUsers()
        call.respond(allUsers)
    }
}