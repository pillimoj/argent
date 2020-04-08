package argent.util

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.httpMethod
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.LoggerFactory
import java.util.UUID

val utilLogger = LoggerFactory.getLogger("argent.util")

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
        utilLogger.error("endpoint called with bad method", e("requiredMethod" to method, "actualMethod" to call.request.httpMethod))
        throw MethodNotAllowedException()
    }
}