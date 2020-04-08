package components.shared

import react.RBuilder
import react.RProps
import react.ReactElement
import react.child
import react.functionalComponent
import styled.styledDiv

interface ErrorMessageProps: RProps {
    var message: String
}

val ErrorMessage = functionalComponent<ErrorMessageProps> {props ->
    styledDiv {
        +props.message
    }
}

fun RBuilder.errorMessage(handler: ErrorMessageProps.() -> Unit): ReactElement {
    return child(ErrorMessage) {
        this.attrs(handler)
    }
}