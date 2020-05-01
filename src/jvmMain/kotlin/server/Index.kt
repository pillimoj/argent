package argent.server

import argent.api.RouteHandler
import io.ktor.application.call
import io.ktor.html.respondHtml
import kotlinx.html.*

val indexHandler: RouteHandler = {
    call.respondHtml { renderIndex() }
}

fun HTML.renderIndex(){
    head{
        title = "Argent"
        link(rel="stylesheet", href="/index.css")
        link(rel="stylesheet", href="https://fonts.googleapis.com/css2?family=Jost&display=swap")
    }
    body {
        div { id = "root" }
        script(src = "/argent.js"){}
    }
}