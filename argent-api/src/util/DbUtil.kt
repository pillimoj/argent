package argent.util

import argent.config.Config
import argent.config.DbConf
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import javax.sql.DataSource

object DataBases {
    object Argent {
        val queryLogger = logger
        val dataSource by lazy { getDataSource(Config.argentDb) }
        val database by lazy { Database.connect(dataSource) }
    }
}

private fun getDataSource(dbConf: DbConf): DataSource {
    val logger = DataBases.logger
    logger.info(
        "Connecting to db",
        e("host" to dbConf.host, "database" to dbConf.database, "user" to dbConf.user)
    )
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://${dbConf.host}:${dbConf.port}/${dbConf.database}"
        username = dbConf.user
        password = dbConf.password
        maximumPoolSize = 2
        minimumIdle = 2
        connectionTimeout = 10000 // 10 seconds
        idleTimeout = 600000 // 10 minutes
        maxLifetime = 1800000 // 30 minutes
    }
    return HikariDataSource(config)
}

suspend fun <T> Database.transaction(block: Transaction.() -> T): T {
    return suspendedTransactionAsync(Dispatchers.DB, db = this) {
        block()
    }.await()
}

object QueryLogger : SqlLogger {
    override fun log (context: StatementContext, transaction: Transaction) {
            DataBases.Argent.queryLogger.debug(
                "Querying argent db",
                e("query" to context.expandArgs(TransactionManager.current()))
            )
    }
}
