package argent.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import javax.sql.DataSource

suspend fun <T> DataSource.asyncConnection(block: suspend Connection.() -> T): T {
    return withContext(Dispatchers.IO) {
        block(connection)
    }
}
