package components

import api.getLists
import helpers.builder
import react.RProps
import react.dom.div
import react.functionalComponent
import react.router.dom.routeLink
import useApi

val Lists = functionalComponent<RProps> {

    val (listsState) = useApi { getLists() }
    listsState.data?.forEach {
        routeLink("/lists/${it.id}") {
            div {
                +it.name
            }
        }
    } ?:div{
        +"something else"
    }
}

val lists = Lists.builder()
