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
import argent.server.features.configureCORS
import argent.server.features.configureStatusPages
import argent.server.features.installCallLogging
import argent.util.argentJson
import argent.util.database.DataBases
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.forwardedheaders.ForwardedHeaders
import io.ktor.server.plugins.hsts.HSTS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.main() {
    val userDataStore = UserDataStore(DataBases.Argent.dbPool)
    val checklistDataStore = ChecklistDataStore(DataBases.Argent.dbPool)
    val gameDataStore = GameDatastore(DataBases.Argent.dbPool)

    val adminController = AdminController(userDataStore)
    val checklistController = ChecklistController(checklistDataStore, userDataStore)
    val usersController = UsersController(userDataStore)
    val gameController = GameController(gameDataStore)

    val configureAuth: AuthenticationConfig.() -> Unit = { argentAuthJwt { } }

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
    configureAuth: AuthenticationConfig.() -> Unit
) {
    installCallLogging()
    install(ContentNegotiation) { json(argentJson) }
    install(Compression) { gzip { } }
    install(ForwardedHeaders)
    install(HSTS)
    install(CORS) { configureCORS() }
    install(StatusPages) { configureStatusPages() }
    install(Authentication) { configureAuth() }

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
