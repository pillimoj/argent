package components

import com.benasher44.uuid.uuidFrom
import components.checklists.list
import components.checklists.lists
import kotlinx.css.*
import org.w3c.dom.url.URLSearchParams
import react.*
import react.dom.div
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch
import styled.StyleSheet
import styled.css
import styled.styledDiv

interface IdRouteProps : RProps {
    var id: String
}

val styles = CSSBuilder().apply {
    body {
        margin(0.px)
        padding(0.px)
    }
}



val App = functionalComponent<RProps> {
    styledDiv {
        css {
            +AppStyles.app
            +AppStyles.page
        }
        nav {}
        styledDiv {
            css {
                marginTop = 5.rem
            }
            browserRouter {
                switch {
                    route("/", exact = true) {
                        lists { }
                    }
                    route<IdRouteProps>("/lists/:id") { props ->
                        list(id = uuidFrom(props.match.params.id)) {}
                    }
                    route<RProps>("/spinner") { props ->
                        val colorParam = URLSearchParams(props.location.search).get("color")?.let { Color(it) }
                        val sizeParam = URLSearchParams(props.location.search).get("size")?.let { LinearDimension(it) }
                        div {
                            +"Spinner color: $colorParam  size: $sizeParam"
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
    }
}

fun RBuilder.app(handler: RHandler<RProps>): ReactElement {
    return child(
        functionalComponent = App,
        handler = handler
    )
}


object AppStyles: StyleSheet("App", isStatic = true){
    val page by css {
        maxWidth = 50.rem
        media("(max-width: 40rem)"){
            maxWidth = 38.rem
        }
    }

    val app by css {
        margin(0.px, LinearDimension.auto)
    }
}