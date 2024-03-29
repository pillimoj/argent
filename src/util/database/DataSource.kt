package argent.util.database

import argent.server.CloudSqlDbConf
import argent.server.Config
import argent.server.ConfigurationError
import argent.server.DbConf
import argent.server.TCPDbConf
import argent.util.WithLogger
import argent.util.extra
import argent.util.logger
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object DataBases : WithLogger {
    object Argent {
        val dbPool by lazy { getDataSource(Config.argentDb) }
    }
}

private fun getDataSource(dbConf: DbConf): HikariDataSource {
    return when {
        dbConf.tcpConf != null -> getDataSourceTcp(dbConf.database, dbConf.user, dbConf.password, dbConf.tcpConf)
        dbConf.cloudSqlDbConf != null -> getDataSourceCloudSql(dbConf.database, dbConf.user, dbConf.password, dbConf.cloudSqlDbConf)
        else -> throw ConfigurationError("argent-db")
    }
}

private fun getDataSourceTcp(database: String, user: String, password: String, conf: TCPDbConf): HikariDataSource {
    val logger = DataBases.logger
    logger.info(
        "Connecting to db",
        extra("host" to conf.host, "database" to database, "user" to user)
    )
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://${conf.host}:${conf.port}/$database"
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

private fun getDataSourceCloudSql(database: String, user: String, password: String, conf: CloudSqlDbConf): HikariDataSource {
    val logger = DataBases.logger
    logger.info(
        "Connecting to db",
        extra("instance" to conf.connectionName, "database" to database, "user" to user)
    )
    val config = HikariConfig().apply {
        this.jdbcUrl = "jdbc:postgresql:///$database"
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
