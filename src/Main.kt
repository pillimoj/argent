package argent

import argent.server.Config
import argent.server.DataBases
import argent.server.main
import argent.util.runMigrations
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() {
    Config // Initialize
    val logger: Logger = LoggerFactory.getLogger("argent")
    runMigrations(DataBases.Argent.dbPool)

    val server =
        embeddedServer(
            factory = Netty,
            module = Application::main,
            port = Config.port,
            configure = {
                responseWriteTimeoutSeconds = 30
            },
        )
    logger.info("Starting embedded server...")
    server.start()
}
