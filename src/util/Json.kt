package argent.util

import kotlinx.serialization.json.Json

val argentJson = Json{
    ignoreUnknownKeys = true
}
/*

@ImplicitReflectionSerializer
fun Map<*, *>.toJsonObject(): JsonObject = JsonObject(map {(k,v) ->
    k.toString() to v.toJsonElement()
}.toMap())

@ImplicitReflectionSerializer
fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is Map<*, *> -> this.toJsonObject()
    is Iterable<*> -> JsonArray(this.map { it.toJsonElement() })
    is Array<*> -> JsonArray(this.map { it.toJsonElement() })
    else -> JsonPrimitive(this.toString()) // Or throw some "unsupported" exception?
}*/
