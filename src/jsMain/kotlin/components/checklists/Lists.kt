package components.checklists

import Requesting
import Success
import api.getLists
import components.shared.SharedStyles
import components.shared.errorMessage
import components.shared.spinner
import helpers.builderFunc
import react.RProps
import react.dom.div
import react.functionalComponent
import react.router.dom.routeLink
import styled.css
import styled.styledDiv
import useApi

val Lists = functionalComponent<RProps> {

    val (listsState) = useApi { getLists() }
    when (listsState) {
        is Requesting -> spinner { }
        is Error -> errorMessage { +"Could not load checklists" }
        is Success -> listsState.loadedData.forEach {
            styledDiv { css { +SharedStyles.link }
                routeLink("/lists/${it.id}") {
                    div {
                        +it.name
                    }
                }
            }
        }
    }
}

val lists = Lists.builderFunc()
