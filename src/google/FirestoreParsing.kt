package argent.google

import argent.util.defaultObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.QuerySnapshot

inline fun <reified T> QuerySnapshot.parseList(): List<T> {
    return map { it.data.parse() }
}

inline fun <reified T> QuerySnapshot.parseOne(): T? {
    val qds = firstOrNull() ?: return null
    return qds.data.parse()
}

inline fun <reified T> Map<String, Any?>.parse(): T {
    return defaultObjectMapper.readValue(defaultObjectMapper.writeValueAsString(this))
}

inline fun <reified T> DocumentSnapshot.parseOne(): T? {
    return data?.parse()
}

fun Any.storable(): Map<*, *> = defaultObjectMapper.convertValue(this, Map::class.java)
