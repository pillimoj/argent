package argent.api

import argent.data.users.User
import argent.data.users.UserRole
import argent.server.ForbiddenException
import argent.server.InternalServerError
import argent.util.requireMethod
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import io.ktor.websocket.DefaultWebSocketServerSession

typealias CallContext = PipelineContext<Unit, ApplicationCall>
typealias RouteHandler = suspend CallContext.(Unit) -> Unit
typealias PrincipalHandler = suspend CallContext.(User) -> Unit
typealias WsHandler = suspend DefaultWebSocketServerSession.() -> Unit
typealias PrincipalWsHandler = suspend DefaultWebSocketServerSession.(User) -> Unit


fun unAuthedHandler(method: HttpMethod, block: RouteHandler): RouteHandler = {
    requireMethod(method)
    block(Unit)
}

fun authedHandler(method: HttpMethod, block: PrincipalHandler): RouteHandler = {
    requireMethod(method)
    val principal = call.principal<User>() ?: throw InternalServerError("No principal in api handler")
    block(principal)
}

fun authedWsHandler(block: PrincipalWsHandler): WsHandler = {
    val principal = call.principal<User>() ?: throw InternalServerError("No principal in api WS handler")
    block(principal)
}

fun adminHandler(method: HttpMethod, block: PrincipalHandler): RouteHandler = {
    requireMethod(method)
    val principal = call.principal<User>() ?: throw InternalServerError("No principal in api handler")
    if (principal.role != UserRole.Admin) {
        throw ForbiddenException()
    }
    block(principal)
}

fun notImplementedAuthedHandler(method: HttpMethod): RouteHandler = {
    requireMethod(method)
    call.principal<User>() ?: throw InternalServerError("No principal in api handler")
    call.respond(HttpStatusCode.NotImplemented)
}