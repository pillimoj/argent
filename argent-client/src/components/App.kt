package components

import components.shared.spinner
import kotlinx.css.Color
import kotlinx.css.LinearDimension
import kotlinx.css.margin
import kotlinx.css.maxWidth
import kotlinx.css.px
import kotlinx.css.rem
import org.w3c.dom.url.URLSearchParams
import react.RBuilder
import react.RProps
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch
import styled.css
import styled.styledDiv

interface IdProps : RProps {
    var id: String
}

fun RBuilder.app() =
    styledDiv {
        css {
            maxWidth = 80.rem
            margin(0.px, LinearDimension.auto)
        }
        nav { }
        browserRouter {
            switch {
                route("/", exact = true) {
                    lists { }
                }
                route<IdProps>("/lists/:id") { props ->
                    list {
                        id = props.match.params.id
                    }
                }
                route<RProps>("/spinner") { props ->
                    val colorParam = URLSearchParams(props.location.search).get("color")?.let { Color(it) }
                    val sizeParam = URLSearchParams(props.location.search).get("size")?.let { LinearDimension(it) }
                    spinner {
                        colorParam?.also { color = colorParam }
                        sizeParam?.also { size = sizeParam }
                    }

                }
                // route("/login", strict = true) {
                //     login(providers = listOf("plain", "facebook"))
                //     a(href = "#/") {
                //         +"Back"
                //     }
                // }
                // route<IdProps>("/user/:id") { props ->
                //     div {
                //         +"User id: ${props.match.params.id}"
                //     }
                // }
                // redirect(from = "/redirect", to = "/redirected")
            }
        }
    }
