package components.shared

import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet
import styled.animation


private fun CSSBuilder.bounceBaseStyle() {
    width = 100.pct
    height = 100.pct
    borderRadius = 50.pct
    backgroundColor = Color("#333")
    opacity = 0.6;
    position = Position.absolute;
    top = 0.px;
    left = 0.px
}

private fun CSSBuilder.bounceAnimation(delay: Time) {
    animation(2.s, Timing.easeInOut, delay, IterationCount.infinite) {
        0 { transform { scale(0.0) } }
        50 { transform { scale(1.0) } }
        100 { transform { scale(0.0) } }
    }
}

object SpinnerStyles : StyleSheet("SpinnerStyles", isStatic = true) {
    val spinner by css {
        position = Position.relative
    }

    val doubleBounce1 by css {
        bounceBaseStyle()
        bounceAnimation(0.s)
    }
    val doubleBounce2 by css {
        bounceBaseStyle()
        bounceAnimation(1.s)
    }
}