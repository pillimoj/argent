package argent.server

import argent.api.RouteHandler
import io.ktor.application.call
import io.ktor.html.respondHtml
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.script
import kotlinx.html.title

val indexHandler: RouteHandler = {
    call.respondHtml { renderIndex() }
}

fun HTML.renderIndex(){
    head{
        title = "Argent"
    }
    body {
        div { id = "root" }
        script(src = "/argent-client.js"){}
    }
}