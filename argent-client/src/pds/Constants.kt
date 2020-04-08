package pds

import kotlinx.css.Color
import kotlinx.css.LinearDimension
import kotlinx.css.properties.BoxShadow
import kotlinx.css.properties.BoxShadows
import kotlinx.css.properties.Time
import kotlinx.css.properties.Timing
import kotlinx.css.properties.cubicBezier
import kotlinx.css.properties.s
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.css.rgba

enum class FONTS(val font: String) {
    PRIMARY("Epidemic, sans-serif"),
    LEGACY("AzoSans, 'Azo Sans', Helvetica, Arial, sans-serif"),
};

enum class BREAKPOINTS(val bp: LinearDimension) {
    SMALL(22.rem),
    MEDIUM(30.rem),
    LARGE(60.rem),
}

enum class COLORS(val color: Color) {
    BLACK(Color("#000")),
    WHITE(Color("#FFF")),
    RED(Color("#C75150")),
    DARK_RED(Color("#9F4140")),
    GREEN(Color("#88BB76")),
    DARK_GRAY(Color("#AAAAAA")),
    LIGHT_GRAY(Color("#F1F1F1")),
    PRIMARY(Color("#00A4EB")),
    TRANSPARENT_OVERLAY(Color("gba(0, 0, 0, 0.2)")),
    COMPLEMENTARY_YELLOW(Color("#FFA800")),
    COMPLEMENTARY_ORANGE(Color("#FF561B")),
    COMPLEMENTARY_GREEN(Color("#013000")),
    COMPLEMENTARY_CYAN(Color("#00EBDD")),
    GRAY_1(Color("#333333")),
    GRAY_2(Color("#767676")),
    GRAY_3(Color("#EAEAEA")),
};

enum class TIMINGS(val timing: Time) {
    MOVE_IN(0.3.s),
    MOVE_OUT(0.2.s),
    MOVE_IN_OUT(0.2.s),
};

enum class EASINGS(val easing: Timing) {
    MOVE_IN(cubicBezier(0.215, 0.61, 0.355, 1.0)),
    MOVE_OUT(cubicBezier(0.55, 0.055, 0.675, 0.19)),
    MOVE_IN_OUT(cubicBezier(0.645, 0.045, 0.355, 1.0)),
};

enum class TAG_TYPES {
    PRIMARY,
    SECONDARY,
};

val boxshadow by lazy {
    val bs = BoxShadows()
    bs += BoxShadow(false, 0.px, 0.px, 4.px, 0.px, rgba(0, 0, 0, 0.05))
    bs += BoxShadow(false, 0.px, 0.px, 8.px, 0.px, rgba(0, 0, 0, 0.04))
    bs += BoxShadow(false, 0.px, 0.px, 16.px, 0.px, rgba(0, 0, 0, 0.03))
    bs += BoxShadow(false, 0.px, 0.px, 32.px, 0.px, rgba(0, 0, 0, 0.02))
    bs
}

enum class SHADOWS(val shadow: BoxShadows) {
    BOX(boxshadow)
};

enum class POSITIONS {
    TOP,
    TOP_LEFT,
    BOTTOM,
    BOTTOM_LEFT,
    LEFT,
    RIGHT,
    CENTER,
}

enum class VERTICAL_TAB_VARIANTS {
    DEFAULT,
    TITLE,
};
