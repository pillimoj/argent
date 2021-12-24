package argent.util.database

import argent.util.toLocalDateTime
import io.ktor.util.date.GMTDate
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

internal fun PreparedStatement.setArgument(connection: Connection, position: Int, arg: Any?) = when (arg) {
    is StringArray -> setArray(position, connection.createArrayOf("text", arg.strings.toTypedArray()))
    is UUIDArray -> setArray(position, connection.createArrayOf("uuid", arg.uuids.toTypedArray()))
    is String -> setString(position, arg)
    is Double -> setDouble(position, arg)
    is GMTDate -> setObject(position, arg.toLocalDateTime())
    is LocalDateTime -> setObject(position, arg)
    is LocalTime -> setObject(position, arg)
    is UUID -> setObject(position, arg)
    is Int -> setInt(position, arg)
    is Long -> setLong(position, arg)
    is Boolean -> setBoolean(position, arg)
    is Enum<*> -> setString(position, arg.name)
    null -> setObject(position, null)
    else -> throw Exception("Argument not supported: $arg, $position")
}

internal fun <T> Connection.statement(
    query: String,
    args: List<Any?>,
    closeConnection: Boolean = true,
    block: PreparedStatement.() -> T
): T {
    val statement = prepareStatement(query).apply {
        args.forEachIndexed { index: Int, arg: Any? ->
            val pos = index + 1
            setArgument(this@statement, pos, arg)
        }
    }
    return try {
        block(statement)
    } finally {
        statement.close()
        if (closeConnection) close()
    }
}

internal fun Connection.executeUpdate(query: String, args: List<Any?>, closeConnection: Boolean = true) =
    statement(query, args, closeConnection) {
        executeUpdate()
    }

internal fun <T> Connection.executeQuery(
    query: String,
    args: List<Any?>,
    closeConnection: Boolean,
    block: (ResultSet) -> T
): T = statement(query, args, closeConnection) {
    val resultSet = executeQuery()
    return@statement resultSet.use { rs ->
        block(rs)
    }
}
