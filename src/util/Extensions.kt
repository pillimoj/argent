package argent.util

import argent.server.MethodNotAllowedException
import argent.server.NotFoundException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.httpMethod
import io.ktor.util.date.GMTDate
import io.ktor.util.pipeline.PipelineContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

inline fun <reified T : Enum<T>> safeEnumValueOf(string: String): T? =
    try {
        enumValueOf<T>(string)
    } catch (e: IllegalArgumentException) {
        null
    }

fun String.toUUIDsafe(): UUID? = runCatching {
    UUID.fromString(this)
}.getOrNull()

fun PipelineContext<Unit, ApplicationCall>.pathParam(name: String = "id"): UUID {
    return call.parameters[name]?.toUUIDsafe() ?: throw NotFoundException("No id")
}

fun PipelineContext<Unit, ApplicationCall>.pathParamSafe(name: String = "id"): UUID? {
    return call.parameters[name]?.toUUIDsafe()
}

fun PipelineContext<Unit, ApplicationCall>.requireMethod(method: HttpMethod) {
    if (call.request.httpMethod != method){
        throw MethodNotAllowedException()
    }
}

fun LocalDateTime.toGMTDate(): GMTDate = GMTDate(toEpochSecond(ZoneOffset.UTC))