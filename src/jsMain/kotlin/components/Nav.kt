package components

import Failed
import Requesting
import Success
import api.getUser
import components.shared.Colors
import components.shared.errorMessage
import components.shared.spinner
import helpers.builderFunc
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.deg
import kotlinx.css.properties.rotate
import kotlinx.css.properties.transform
import react.RProps
import react.dom.div
import react.functionalComponent
import styled.*
import useApi

val Nav = functionalComponent<RProps> {

    val (userState) = useApi { getUser() }
    styledDiv { css { +AppStyles.page; +NavStyles.container }
        styledA (href = "/") { css {+NavStyles.link}
            styledDiv { css { +NavStyles.icon }
                div {
                    +"A"
                }
            }
        }
        styledH1 { css { +NavStyles.header }
            +"Argent"
        }
        div {
            when (userState) {
                is Failed -> errorMessage { +"Could not fetch user" }
                is Requesting -> spinner {}
                is Success -> +userState.loadedData.email
            }
        }
    }
}

val nav = Nav.builderFunc()

private object NavStyles : StyleSheet("Nav", isStatic = true) {
    val container by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns(5.rem, 1.fr, 12.rem)
        alignItems = Align.flexEnd
        marginBottom = 2.rem
        width = 100.pct
        height = 5.rem
        position = Position.fixed
        top = 0.rem
    }

    val icon by css {
        transform { rotate(180.deg) }
        height = 5.rem
        fontSize = 5.rem
        display = Display.flex;
        alignItems = Align.center;
        justifyContent = JustifyContent.center;
        overflow = Overflow.hidden
    }

    val link by css {
        color = Colors.White.color
        textDecoration = TextDecoration.none
    }

    val header by css {
        margin(0.rem)
    }
}