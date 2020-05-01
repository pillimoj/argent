package components.checklists

import ChecklistItemRes
import Failed
import Requesting
import Success
import api.getList
import api.setItemDone
import com.benasher44.uuid.Uuid
import components.shared.errorMessage
import helpers.runAsync
import kotlinext.js.jsObject
import org.w3c.dom.events.Event
import react.*
import useApi

interface ListProps: RProps {
    var id: Uuid
}

val List = functionalComponent<ListProps> { props ->
    val (listState, fetchList) = useApi { getList(props.id) }
    val onItemCheck: (ChecklistItemRes) -> (Event) -> Unit = { item ->
        {_ ->
            runAsync {
                setItemDone(item.id, !item.done)
                fetchList()
            }
        }
    }

    when(listState){
        is Success -> listItems(items = listState.loadedData.items, onItemClick = onItemCheck){}
        is Requesting -> +"loading..."
        is Failed -> errorMessage {
           +"Failed getting list: ${listState.error}"
        }
    }
}

fun RBuilder.list(id: Uuid, handler: RHandler<ListProps>): ReactElement {
    return child(
        functionalComponent = List,
        props = jsObject {
            this.id = id
        },
        handler = handler
    )
}


interface ListItemsProps: RProps {
    var items: List<ChecklistItemRes>
    var onItemClick: (ChecklistItemRes) -> (Event) -> Unit
}

val ListItems = functionalComponent<ListItemsProps> { props ->
    val itemsSorted = props.items
        .sortedByDescending { it.createdAt }
        .partition { !it.done }
        .toList()
        .flatten()
    itemsSorted.forEach {
        listItem(item = it, onClick = props.onItemClick(it)){}
    }
}

fun RBuilder.listItems(items: List<ChecklistItemRes>, onItemClick: (ChecklistItemRes) -> (Event) -> Unit, handler: RHandler<ListItemsProps>): ReactElement {
    return child(
        functionalComponent = ListItems,
        props = jsObject {
            this.items = items
            this.onItemClick = onItemClick
        },
        handler = handler
    )
}