package argent.api.controllers

import argent.api.respondOk
import argent.api.unAuthedHandler
import argent.google.MyGceInstance
import argent.server.InternalServerError
import argent.util.WithLogger
import io.ktor.http.HttpMethod
import io.ktor.server.application.call
import io.ktor.server.response.respond

class GceVmController: WithLogger {
    val status = unAuthedHandler(HttpMethod.Get) {
        val instance = MyGceInstance.getStatus() ?: throw InternalServerError("Failed getting instance status")
        call.respond(instance)
    }

    val start = unAuthedHandler(HttpMethod.Post) {
        MyGceInstance.startInstance()
        call.respondOk()
    }

    val stop = unAuthedHandler(HttpMethod.Post) {
        MyGceInstance.stopInstance()
        call.respondOk()
    }
}