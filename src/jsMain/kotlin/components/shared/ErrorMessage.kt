package components.shared

import kotlinx.css.backgroundColor
import kotlinx.css.borderRadius
import kotlinx.css.padding
import kotlinx.css.rem
import react.*
import styled.css
import styled.styledDiv

interface ErrorMessageProps: RProps {
    var message: String
}

val ErrorMessage = functionalComponent<ErrorMessageProps> {props ->
    styledDiv {
        css {
            backgroundColor = Colors.ErrorBG.color
            padding(1.5.rem)
            borderRadius = 0.5.rem
        }
        props.children()
    }
}

fun RBuilder.errorMessage(handler: RHandler<ErrorMessageProps>): ReactElement {
    return child(
        functionalComponent = ErrorMessage,
        handler = handler
    )
}