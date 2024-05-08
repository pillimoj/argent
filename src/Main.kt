package argent

import argent.server.Config
import argent.server.main
import argent.util.database.DataBases
import argent.util.database.runMigrations
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    Config // Initialize
    runMigrations(DataBases.Argent.dbPool)
    Config.initDevAdmin()

    val server =
        embeddedServer(
            factory = Netty,
            module = Application::main,
            port = Config.port,
            configure = {
                responseWriteTimeoutSeconds = 30
            },
        )
    server.start(wait = true)
}
