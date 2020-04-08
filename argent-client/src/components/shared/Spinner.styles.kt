package components.shared

import kotlinx.css.Float
import kotlinx.css.Position
import kotlinx.css.float
import kotlinx.css.height
import kotlinx.css.left
import kotlinx.css.opacity
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.FillMode
import kotlinx.css.properties.IterationCount
import kotlinx.css.properties.Timing
import kotlinx.css.properties.deg
import kotlinx.css.properties.perspective
import kotlinx.css.properties.rotateX
import kotlinx.css.properties.rotateY
import kotlinx.css.properties.rotateZ
import kotlinx.css.properties.s
import kotlinx.css.properties.scale
import kotlinx.css.properties.transform
import kotlinx.css.px
import kotlinx.css.top
import kotlinx.css.width
import styled.StyleSheet
import styled.animation

object SpinnerStyles : StyleSheet("ComponentStyles", isStatic = true) {
    val foldingCube by css {
        position = Position.relative
        transform { rotateZ(45.deg) }
    }
    val cube by css {
        float = Float.left
        width = 50.pct
        height = 50.pct
        position = Position.relative
        transform { scale(1.1) }
        before {
            +""
            position = Position.absolute
            top = 0.px
            left = 0.px
            width = 100.pct
            height = 100.pct
            animation(1.6.s, iterationCount = IterationCount.infinite, timing = Timing.linear, fillMode = FillMode.both) {
                0 {
                    transform { perspective(140.px); rotateX((-180).deg); }
                    opacity = 0
                }
                10 {
                    transform { perspective(140.px); rotateX((-180).deg); }
                    opacity = 0
                }
                25 {
                    transform { perspective(140.px); rotateX(0.deg); }
                    opacity = 1
                }
                75 {
                    transform { perspective(140.px); rotateX(0.deg); }
                    opacity = 1
                }
                90{
                    transform { perspective(140.px); rotateY(180.deg); }
                    opacity = 0
                }
                100{
                    transform { perspective(140.px); rotateY(180.deg); }
                    opacity = 0
                }
            }
            put("transform-origin", "100% 100%")
        }
    }
    val cube2 by css {
        transform { scale(1.1); rotateZ(90.deg); }
        before { put("animation-delay", "0.2s") }
    }
    val cube3 by css {
        transform { scale(1.1); rotateZ(180.deg); }
        before { put("animation-delay", "0.4s") }
    }
    val cube4 by css {
        transform { scale(1.1); rotateZ(270.deg); }
        before { put("animation-delay", "0.6s") }
    }
}