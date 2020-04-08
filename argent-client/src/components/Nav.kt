package components

import Failed
import Requesting
import Success
import api.getUser
import components.shared.errorMessage
import components.shared.spinner
import helpers.builder
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.display
import kotlinx.css.justifyContent
import kotlinx.css.marginBottom
import kotlinx.css.rem
import kotlinx.css.width
import react.RProps
import react.dom.a
import react.dom.div
import react.functionalComponent
import styled.css
import styled.styledDiv
import styled.styledImg
import useApi

val Nav = functionalComponent<RProps> {

    val (userState) = useApi { getUser() }
    styledDiv {
        css {
            display = Display.flex
            justifyContent = JustifyContent.spaceBetween
            marginBottom = 2.rem
        }
        a(href = "/") {
            styledImg(src = "/logo.png") {
                css {
                    width = 5.rem
                }

            }
        }
        div {
            when (userState) {
                is Failed -> errorMessage{message = "Could not fetch user"}
                is Requesting -> spinner{}
                is Success ->  +userState.loadedData.email
            }
        }
    }
}

val nav = Nav.builder()