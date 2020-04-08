package pds

import helpers.typeSafeBuilder
import kotlinx.css.Align
import kotlinx.css.BorderStyle
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.LinearDimension
import kotlinx.css.Position
import kotlinx.css.Visibility
import kotlinx.css.alignItems
import kotlinx.css.backgroundColor
import kotlinx.css.borderColor
import kotlinx.css.borderLeftColor
import kotlinx.css.borderRadius
import kotlinx.css.borderStyle
import kotlinx.css.borderTopColor
import kotlinx.css.borderWidth
import kotlinx.css.display
import kotlinx.css.height
import kotlinx.css.justifyContent
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.AnimationDirection
import kotlinx.css.properties.FillMode
import kotlinx.css.properties.IterationCount
import kotlinx.css.properties.KeyframesBuilder
import kotlinx.css.properties.PlayState
import kotlinx.css.properties.Timing
import kotlinx.css.properties.deg
import kotlinx.css.properties.rotate
import kotlinx.css.properties.s
import kotlinx.css.properties.transform
import kotlinx.css.visibility
import kotlinx.css.width
import react.Children
import react.RProps
import react.children
import react.functionalComponent
import styled.StyleSheet
import styled.animation
import styled.css
import styled.styledSpan

val spin: KeyframesBuilder.() -> Unit = {
    100 {
        transform { rotate(360.deg) }
    }
}

val colorToSpinnerBorder: CSSBuilder.(Color?) -> Unit = { color ->
    borderLeftColor = color ?: Color.inherit
    borderTopColor = color ?: Color.inherit
}

val sizeToSpinnerSize: CSSBuilder.(LinearDimension) -> Unit = { size ->
    width = size
    height = size
    borderWidth = size / 8
}

object SpinnerStyles : StyleSheet("SpinnerStyles", isStatic = true) {
    val container by css {
        alignItems = Align.center
        display = Display.flex;
        height = 100.pct
        justifyContent = JustifyContent.center
        width = 100.pct
    }

    val spinner by css {
        animation(
            0.5.s,
            Timing.linear,
            0.s,
            IterationCount.infinite,
            AnimationDirection.normal,
            FillMode.both,
            PlayState.running
        ) {
            spin()
        }
        backgroundColor = Color.transparent
        borderColor = Color.transparent
        borderRadius = 100.pct
        borderStyle = BorderStyle.solid
    }

    val hiddenText by css {
        visibility = Visibility.hidden
    }
};

interface SpinnerProps : RProps {
    var size: LinearDimension
    var color: Color?
}


val Spinner = functionalComponent<SpinnerProps> { props ->
    val hasChildren = Children.count(props.children) > 0
    styledSpan {
        css { +SpinnerStyles.container }
        styledSpan {
            css {
                +SpinnerStyles.spinner
                colorToSpinnerBorder(props.color)
                sizeToSpinnerSize(props.size)
                position = if (hasChildren) Position.absolute else Position.static
            }
        }
        if(hasChildren){
            styledSpan {
                css { SpinnerStyles.hiddenText }
                props.children()
            }
        }
    }
}

val spinner = Spinner.typeSafeBuilder()
