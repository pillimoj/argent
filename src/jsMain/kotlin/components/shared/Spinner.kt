package components.shared

import kotlinext.js.jsObject
import kotlinx.css.LinearDimension
import kotlinx.css.height
import kotlinx.css.rem
import kotlinx.css.width
import react.*
import styled.css
import styled.styledDiv

interface SpinnerProps: RProps {
    var size: LinearDimension
}

val Spinner = functionalComponent<SpinnerProps> {props ->
   styledDiv { css{+SpinnerStyles.spinner; width = props.size; height = props.size}
       styledDiv { css { +SpinnerStyles.doubleBounce1 } }
       styledDiv { css { +SpinnerStyles.doubleBounce2 } }
   }
}

fun RBuilder.spinner(size: LinearDimension = 3.rem, handler: RHandler<SpinnerProps>): ReactElement {
    return child(
        functionalComponent = Spinner,
        props = jsObject { this.size = size },
        handler = handler
    )
}