package pds

import kotlinext.js.jsObject
import kotlinx.css.em
import react.RProps
import react.functionalComponent
import styled.css
import styled.styledSpan

class ButtonProps(
    var variant: ButtonVariant = ButtonVariant.Primary,
    var size: ButtonSize = ButtonSize.Small,
    var isDisabled: Boolean = false,
    var isLoading: Boolean = false,
    var shouldFillContainer: Boolean = false,
    var hasArrow: Boolean = false,
    var component: String = "button"
) : RProps

val Button = functionalComponent<ButtonProps> { props ->

    if(isLoading) {
        spinner(jsObject<SpinnerProps> { size = 1.em })
        if(props.hasArrow){ styledSpan {css { ButtonStyles.arrow }} }
    {children}
    </Spinner>
    ) : (
    <span>
    {arrow}
    {children}
    </span>
    );

    return jsx(component, {
        ...props,
        children: newChildren,
        disabled: !!isDisabled,
        css: [styles.button, styles[variant], styles[size], shouldFillContainer && styles.fillContainer],
    });
}
