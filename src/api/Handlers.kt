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
import io.ktor.util.pipeline.PipelineContext

typealias CallContext = PipelineContext<Unit, ApplicationCall>
typealias RouteHandler = suspend CallContext.(Unit) -> Unit
typealias PrincipalHandler = suspend CallContext.(User) -> Unit

fun unAuthedHandler(method: HttpMethod, block: RouteHandler): RouteHandler = {
    requireMethod(method)
    block(Unit)
}

fun authedHandler(method: HttpMethod, block: PrincipalHandler): RouteHandler = {
    requireMethod(method)
    val principal = call.principal<User>() ?: throw InternalServerError("No principal in api handler")
    block(principal)
}

fun adminHandler(method: HttpMethod, block: PrincipalHandler): RouteHandler = {
    requireMethod(method)
    val principal = call.principal<User>() ?: throw InternalServerError("No principal in api handler")
    if(principal.role != UserRole.Admin){
        throw ForbiddenException()
    }
    block(principal)
}