package helpers

import react.*

fun FunctionalComponent<RProps>.builderFunc(): RBuilder.(builder: RHandler<RProps>) -> ReactElement {
    return {handler ->
        child(
            functionalComponent = this@builderFunc,
            handler = handler
        )
    }
}
