package components

import Failed
import Requesting
import Success
import api.ChecklistItemRes
import api.getList
import api.setItemDone
import components.shared.errorMessage
import components.shared.spinner
import helpers.builder
import helpers.runAsync
import kotlinext.js.jsObject
import kotlinx.css.Color
import org.w3c.dom.events.Event
import react.RProps
import react.functionalComponent
import useApi

interface ListProps: RProps {
    var id: String
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
        is Success -> listItems{
            items = listState.loadedData.items
            onItemClick = onItemCheck
        }
        is Requesting -> spinner {
            color = Color.pink
        }
        is Failed -> errorMessage {
           message = ""
        }
    }
}

val list = List.builder()


interface ListItemsProps: RProps {
    var items: Array<ChecklistItemRes>
    var onItemClick: (ChecklistItemRes) -> (Event) -> Unit
}

val ListItems = functionalComponent<ListItemsProps> { props ->
    val itemsSorted = props.items
        .sortedByDescending { it.createdAt }
        .partition { !it.done }
        .toList()
        .flatten()
    itemsSorted.forEach {
        listItem2(jsObject {
            item = it
            onClick = props.onItemClick(it)
        }){}
    }
}

val listItems = ListItems.builder()