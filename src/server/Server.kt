package argent.server

import argent.api.controllers.AdminController
import argent.api.controllers.ChecklistController
import argent.api.controllers.GameController
import argent.api.controllers.UsersController
import argent.api.controllers.UtilController
import argent.api.v1Routes
import argent.data.checklists.ChecklistDataStore
import argent.data.game.GameDatastore
import argent.data.users.UserDataStore
import argent.server.features.argentAuthJwt
import argent.server.features.configure
import argent.server.features.installCallLogging
import argent.util.argentJson
import argent.util.database.DataBases
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.HSTS
import io.ktor.features.StatusPages
import io.ktor.features.XForwardedHeaderSupport
import io.ktor.features.gzip
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.websocket.WebSockets
import java.time.Duration

fun Application.main() {
    val userDataStore = UserDataStore(DataBases.Argent.dbPool)
    val checklistDataStore = ChecklistDataStore(DataBases.Argent.dbPool)
    val gameDataStore = GameDatastore(DataBases.Argent.dbPool)

    val adminController = AdminController(userDataStore)
    val checklistController = ChecklistController(checklistDataStore, userDataStore)
    val usersController = UsersController(userDataStore)
    val gameController = GameController(gameDataStore)

    val configureAuth: Authentication.Configuration.() -> Unit = { argentAuthJwt { } }

    mainWithOverrides(
        checklistController,
        usersController,
        adminController,
        gameController,
        configureAuth
    )
}

fun Application.mainWithOverrides(
    checklistController: ChecklistController,
    usersController: UsersController,
    adminController: AdminController,
    gameController: GameController,
    configureAuth: Authentication.Configuration.() -> Unit
) {
    installCallLogging()
    install(ContentNegotiation) { json(argentJson) }
    install(Compression) { gzip() }
    install(XForwardedHeaderSupport)
    install(HSTS)
    install(CORS) { configure() }
    install(StatusPages) { configure() }
    install(Authentication) { configureAuth() }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        get("/ping", UtilController.ping)
        get("/health-check", UtilController.healthCheck)
        route("api/v1") {
            v1Routes(
                checklistController,
                usersController,
                adminController,
                gameController,
            )
        }
    }
}
