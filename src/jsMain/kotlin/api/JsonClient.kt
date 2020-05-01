package api

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlin.browser.window


val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved

val JsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}