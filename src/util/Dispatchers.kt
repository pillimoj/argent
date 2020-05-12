package argent.util

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object Dispatchers {
    val DB = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
}