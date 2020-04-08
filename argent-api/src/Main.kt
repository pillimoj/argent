package argent

import argent.api.ApiController
import argent.checklists.ChecklistDataStore
import argent.config.Config
import argent.server.mainWithDeps
import argent.util.DataBases
import argent.util.e
import argent.util.runMigrations
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.main() {
    // init config
    Config

    runMigrations(DataBases.Argent.dataSource)

    // set up modules
    val store = ChecklistDataStore(DataBases.Argent.database)
    val controller = ApiController(store)

    // configure ktor
    mainWithDeps(controller)
}

fun main() {
    val logger: Logger = LoggerFactory.getLogger("Testing")
    val server =
        embeddedServer(
            factory = Netty,
            module = Application::main,
            port = Config.port,
            watchPaths = Config.watchPaths,
            configure = {
                responseWriteTimeoutSeconds = 30
            }
        )
    logger.info("Starting embedded server...", e("watchPaths" to Config.watchPaths))
    server.start()
}