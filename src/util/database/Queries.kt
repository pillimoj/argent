package argent.util.database

import java.sql.Connection
import java.sql.ResultSet
import java.util.UUID

data class StringArray(val strings: List<String>)
data class UUIDArray(val uuids: List<UUID>)

interface DatabaseQueries {
    /*
    Database pooling with HikariCP is used.
    Connections, PreparedStatements, and ResultSets should be closed!
     */

    fun <T> Connection.executeQuery(query: String, args: List<Any?>, block: (ResultSet) -> T): T =
        executeQuery(query, args, true, block)

    fun Connection.executeUpdate(query: String, args: List<Any?>) =
        executeUpdate(query, args, true)

    fun Connection.executeBatch(query: String, argList: Sequence<List<Any?>>, closeConnection: Boolean = true) {
        val statement = prepareStatement(query)
        argList.forEach { args ->
            args.forEachIndexed { index, arg -> statement.setArgument(this, index + 1, arg) }
            statement.addBatch()
        }
        statement.executeBatch()
        statement.close()
        if (closeConnection) close()
    }

    @Suppress("unused")
    class Transaction(private val connection: Connection) : DatabaseQueries {
        fun executeUpdate(query: String, args: List<Any?>) = connection.executeUpdate(query, args, false)
        fun <T> executeQuery(query: String, args: List<Any?>, block: (ResultSet) -> T): T =
            connection.executeQuery(query, args, false, block)

        fun executeBatch(query: String, argList: Sequence<List<Any?>>) = connection.executeBatch(query, argList, false)
    }

    fun <T> Connection.transaction(block: Transaction.() -> T): T {
        autoCommit = false
        val result = block(Transaction(this))
        commit()
        autoCommit = true
        close()
        return result
    }

    fun <T> parseList(block: (ResultSet) -> T) = { resultSet: ResultSet ->
        val result = mutableListOf<T>()
        while (resultSet.next()) {
            result.add(block(resultSet))
        }
        result.toList()
    }

    fun <T> parse(block: (ResultSet) -> T): (ResultSet) -> T? {
        return { resultSet: ResultSet ->
            if (resultSet.next()) {
                block(resultSet)
            } else {
                null
            }
        }
    }
}
