package components

import api.ChecklistItemRes
import helpers.builder
import helpers.typeSafeBuilder
import kotlinx.css.properties.TextDecorationLine
import kotlinx.css.properties.textDecoration
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.RProps
import react.functionalComponent
import styled.css
import styled.styledDiv


interface ListItemProps : RProps {
    var item: ChecklistItemRes
    var onClick: (Event) -> Unit
}

val ListItem = functionalComponent<ListItemProps> { props ->
    styledDiv {
        css {
            if (props.item.done) textDecoration(TextDecorationLine.lineThrough)
        }
        +props.item.title
        attrs {
            onClickFunction = props.onClick
        }
    }
}

val listItem = ListItem.builder()
val listItem2 = ListItem.typeSafeBuilder()