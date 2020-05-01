package components.shared

import kotlinx.css.a
import kotlinx.css.color
import styled.StyleSheet

object SharedStyles : StyleSheet("SharedStyles", isStatic = true) {
    val link by css {
        a {
            color = Colors.Primary.color
        }
    }
}