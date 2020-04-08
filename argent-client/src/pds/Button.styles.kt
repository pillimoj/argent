package pds

import kotlinx.css.BorderStyle
import kotlinx.css.BoxSizing
import kotlinx.css.Color
import kotlinx.css.Cursor
import kotlinx.css.Display
import kotlinx.css.FontWeight
import kotlinx.css.Image
import kotlinx.css.LinearDimension
import kotlinx.css.Outline
import kotlinx.css.PointerEvents
import kotlinx.css.RuleSet
import kotlinx.css.TextAlign
import kotlinx.css.WhiteSpace
import kotlinx.css.backgroundColor
import kotlinx.css.backgroundImage
import kotlinx.css.borderStyle
import kotlinx.css.borderWidth
import kotlinx.css.boxSizing
import kotlinx.css.color
import kotlinx.css.content
import kotlinx.css.cursor
import kotlinx.css.display
import kotlinx.css.em
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.lineHeight
import kotlinx.css.outline
import kotlinx.css.padding
import kotlinx.css.paddingLeft
import kotlinx.css.paddingRight
import kotlinx.css.pct
import kotlinx.css.pointerEvents
import kotlinx.css.properties.borderBottom
import kotlinx.css.properties.lh
import kotlinx.css.properties.ms
import kotlinx.css.properties.transition
import kotlinx.css.px
import kotlinx.css.quoted
import kotlinx.css.rem
import kotlinx.css.textAlign
import kotlinx.css.whiteSpace
import kotlinx.css.width
import styled.StyleSheet

object ButtonStyles : StyleSheet("ButtonStyles", isStatic = true) {
    val button by css {
        display = Display.inlineBlock
        color = COLORS.WHITE.color
        fontFamily = FONTS.PRIMARY.font
        fontSize = 1.rem
        fontWeight = FontWeight.w500
        lineHeight = 2.5.rem.lh
        paddingLeft = 1.rem
        paddingRight = 1.rem
        whiteSpace = WhiteSpace.nowrap
        borderStyle = BorderStyle.none
        backgroundColor = Color.transparent
        textAlign = TextAlign.center
        width = LinearDimension.auto
        cursor = Cursor.pointer
        children {
            boxSizing = BoxSizing.borderBox
        }
        transition(duration = 100.ms)
        disabled {
            color = COLORS.GRAY_2.color
            cursor = Cursor.default
            pointerEvents = PointerEvents.none
        }
        "&::-moz-focus-inner" {
            borderWidth = 0.rem
        }
    }

    val primary by css {
        backgroundColor = COLORS.PRIMARY.color
        color = COLORS.WHITE.color
        media("hover") {
            hover {
                color = COLORS.BLACK.color
            }
            focus {
                outline = Outline.none
                color = COLORS.BLACK.color
            }
        }
    }
    val secondary by css {
        backgroundColor = COLORS.WHITE.color
        color = COLORS.BLACK.color
        focus {
            outline = Outline.none
            color = COLORS.PRIMARY.color
        }
    }
    val tertiary by css {
        backgroundImage = Image.none
        color = COLORS.PRIMARY.color
        outline = Outline.none
        media("hover") {
            "&:hover > span" {
                borderBottom(2.px, BorderStyle.solid, COLORS.PRIMARY.color)
                padding(0.5.rem, 0.rem)
            }
        }
        "&:focus > span"{
            borderBottom(2.px, BorderStyle.solid, COLORS.PRIMARY.color)
            padding(0.5.rem, 0.rem)
        }
    }

    val arrow by css {
        display = Display.inlineBlock
        textAlign = TextAlign.left
        paddingRight = 0.2.em
    }
    val loading by css {
        before {
            content = "".quoted
        }
    }
    val fillContainer by css {
        width = 100.pct
        display = Display.block
    }
    val S by css {
        fontSize = 1.rem
    }
    val M by css {
        fontSize = 1.25.rem
    }
    val L by css {
        fontSize = 1.5.rem
    }
    val XL by css {
        fontSize = 2.rem
    }
}

enum class ButtonVariant(css: RuleSet) {
    Primary(ButtonStyles.primary),
    Secondary(ButtonStyles.secondary),
    Tertiary(ButtonStyles.tertiary),
    PrimaryText(ButtonStyles.tertiary),
}

enum class ButtonSize(css: RuleSet) {
    Small(ButtonStyles.S),
    Medium(ButtonStyles.M),
    Large(ButtonStyles.L),
    XLarge(ButtonStyles.XL),
}