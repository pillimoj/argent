package helpers

import react.FunctionalComponent
import react.RBuilder
import react.RProps
import react.ReactElement
import react.child


// TODO: This produces good looking code but at the expense of static analysis of props passed to components
// Perhaps it is better to use function that takes props as arg using anonymous object
fun <P: RProps> FunctionalComponent<P>.builder(): RBuilder.(P.() -> Unit) -> ReactElement {
    return {handler ->
        child(this@builder){
            this.attrs(handler)
        }
    }
}

// Better probably need jsObject
fun <P: RProps> FunctionalComponent<P>.typeSafeBuilder(): RBuilder.(P, () -> Unit) -> ReactElement {
    return {props, handler ->
        myRender(this@typeSafeBuilder, props, handler)
    }
}

fun <P: RProps> RBuilder.myRender(comp: FunctionalComponent<P>, props: P, handler:  (() -> Unit) = {}): ReactElement{
    return child(comp, props){}
}