package argent.util

import argent.server.MethodNotAllowedException
import argent.server.NotFoundException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.httpMethod
import io.ktor.util.date.GMTDate
import io.ktor.util.date.toJvmDate
import io.ktor.util.pipeline.PipelineContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

fun String.toUUIDSafe(): UUID? = runCatching {
    UUID.fromString(this)
}.getOrNull()

fun PipelineContext<Unit, ApplicationCall>.pathIdParam(name: String = "id"): UUID {
    return call.parameters[name]?.toUUIDSafe() ?: throw NotFoundException("No id")
}

fun PipelineContext<Unit, ApplicationCall>.requireMethod(method: HttpMethod) {
    if (call.request.httpMethod != method) {
        throw MethodNotAllowedException()
    }
}

fun LocalDateTime.toGMTDate(): GMTDate = GMTDate(toEpochSecond(ZoneOffset.UTC))
fun GMTDate.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(toJvmDate().toInstant(), ZoneOffset.UTC)

inline fun <reified E : Enum<E>> String.asEnum(): E = enumValueOf(this)
