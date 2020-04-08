package api

import kotlinext.js.jsObject
import kotlinx.coroutines.await
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.browser.window
import kotlin.js.json

class FetchError(message: String,val status: Number,val response: dynamic) : Exception(message)


enum class Method {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE
}

class ApiRequestBuilder{
    var method = Method.GET
    var body: Any? = null
}

suspend fun Response.raiseForStatus() {
    if (!ok) {
        throw try {
            val errorResponse: dynamic = json().await()
            FetchError("Request failed", status, errorResponse)
        } catch (e: Exception) {
            val errorResponse = text().await()
            FetchError("Request failed", status, errorResponse)
        }
    }
}

suspend fun <T> request(url: String, builder: ApiRequestBuilder.() -> Unit = {}): T {
    val requestConf = ApiRequestBuilder().apply(builder)
    val responsePromise = window.fetch(url, jsObject<RequestInit> {
        method = requestConf.method.name
        body = requestConf.body?.let { JSON.stringify(it) }
        headers = json(
            "X-Requested-With" to "XMLHttpRequest",
            "Accept" to "application/json",
            "Content-Type" to "application/json"
        )
        credentials = RequestCredentials.INCLUDE
    })
    val response = responsePromise.await()

    response.raiseForStatus()
    val jsonPromise = response.json()
    val json = jsonPromise.await()
    return json.unsafeCast<T>()
}
