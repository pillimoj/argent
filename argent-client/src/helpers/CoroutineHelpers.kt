package helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun runAsync(block: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Default).launch {
        block()
    }
}