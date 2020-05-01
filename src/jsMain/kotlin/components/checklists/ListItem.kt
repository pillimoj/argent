package components.checklists

import ChecklistItemRes
import components.shared.Colors
import kotlinext.js.jsObject
import kotlinx.css.color
import kotlinx.css.properties.TextDecorationLine
import kotlinx.css.properties.textDecoration
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.*
import styled.StyleSheet
import styled.css
import styled.styledDiv


object ListItemStyles: StyleSheet("listItem", isStatic = true){
    val done by css {
        textDecoration(TextDecorationLine.lineThrough)
        color = Colors.Gray.color
    }
}

interface ListItemProps : RProps {
    var item: ChecklistItemRes
    var onClick: (Event) -> Unit
}

val ListItem = functionalComponent<ListItemProps> { props ->
    styledDiv {
        css { if (props.item.done) {+ListItemStyles.done} }
        +props.item.title
        attrs {
            onClickFunction = props.onClick
        }
    }
}

fun RBuilder.listItem(item: ChecklistItemRes, onClick: (Event) -> Unit, handler: RHandler<ListItemProps>): ReactElement {
    return child(
        functionalComponent = ListItem,
        props = jsObject {
            this.item = item
            this.onClick = onClick
        },
        handler = handler
    )
}
