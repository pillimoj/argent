package argent.data

import org.flywaydb.core.Flyway
import javax.sql.DataSource

fun runMigrations(dataSource: DataSource) = Flyway
    .configure()
    .run {
        dataSource(dataSource)
        locations("classpath:migrations")
    }
    .load()
    .migrate()
