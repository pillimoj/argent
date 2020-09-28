package argent.server

import argent.util.extra
import argent.util.logger
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
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

private fun getDataSource(dbConf: DbConf): DataSource{
    return when {
        dbConf.tcpConf != null -> getDataSourceTcp(dbConf.database, dbConf.user, dbConf.password, dbConf.tcpConf)
        dbConf.cloudSqlDbConf != null -> getDataSourceCloudSql(dbConf.database, dbConf.user, dbConf.password, dbConf.cloudSqlDbConf)
        else -> throw ConfigurationError("argent-db")
    }
}

private fun getDataSourceTcp(database:String, user: String, password: String, conf: TCPDbConf): DataSource {
    val logger = DataBases.logger
    logger.info(
        "Connecting to db",
        extra("host" to conf.host, "database" to database, "user" to user)
    )
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://${conf.host}:${conf.port}/${database}"
        username = user
        this.password = password
        maximumPoolSize = 2
        minimumIdle = 2
        connectionTimeout = 10000 // 10 seconds
        idleTimeout = 600000 // 10 minutes
        maxLifetime = 1800000 // 30 minutes
        driverClassName = "org.postgresql.Driver"
    }
    return HikariDataSource(config)
}

private fun getDataSourceCloudSql(database:String, user: String, password: String, conf: CloudSqlDbConf): DataSource {
    val logger = DataBases.logger
    logger.info(
        "Connecting to db",
        extra("instance" to conf.connectionName, "database" to database, "user" to user)
    )
    val config = HikariConfig().apply {
        this.jdbcUrl = "jdbc:postgresql:///${database}"
        this.username = user
        this.password = password
        this.maximumPoolSize = 2
        this.minimumIdle = 2
        this.connectionTimeout = 10000 // 10 seconds
        this.idleTimeout = 600000 // 10 minutes
        this.maxLifetime = 1800000 // 30 minutes
        addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory")
        addDataSourceProperty("cloudSqlInstance", conf.connectionName)
    }
    return HikariDataSource(config)
}

suspend fun <T> Database.transaction(block: Transaction.() -> T): T {
    return suspendedTransactionAsync(kotlinx.coroutines.Dispatchers.IO, db = this) {
        addLogger(QueryLogger)
        block()
    }.await()
}

object QueryLogger : SqlLogger {
    override fun log (context: StatementContext, transaction: Transaction) {
            DataBases.Argent.queryLogger.debug(
                "Querying argent db",
                extra("query" to context.expandArgs(TransactionManager.current()))
            )
    }
}
