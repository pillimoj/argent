package argent.server

import argent.api.controllers.AdminController
import argent.api.controllers.ChatController
import argent.api.controllers.ChecklistController
import argent.api.controllers.GameController
import argent.api.controllers.UsersController
import argent.api.controllers.UtilController
import argent.api.v1Routes
import argent.data.chat.ChatStore
import argent.data.checklists.ChecklistDataStore
import argent.data.game.GameDatastore
import argent.data.users.UserDataStore
import argent.google.ArgentStore
import argent.server.features.argentAuthJwt
import argent.server.features.installCORS
import argent.server.features.installCallLogging
import argent.server.features.installErrorHandling
import argent.util.defaultJackson
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.HSTS
import io.ktor.features.XForwardedHeaderSupport
import io.ktor.features.gzip
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.jackson.jackson
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.websocket.WebSockets
import java.time.Duration

fun Application.main() {
    val db = ArgentStore()
    val userDataStore = UserDataStore(db)
    val checklistDataStore = ChecklistDataStore(db)
    val gameDataStore = GameDatastore(db)
    val chatStore = ChatStore(db)

    val adminController = AdminController(userDataStore)
    val checklistController = ChecklistController(checklistDataStore, userDataStore)
    val usersController = UsersController(userDataStore)
    val gameController = GameController(gameDataStore)
    val chatController = ChatController(chatStore)

    val configureAuth: Authentication.Configuration.() -> Unit = { argentAuthJwt { } }

    mainWithOverrides(
        checklistController,
        usersController,
        adminController,
        gameController,
        chatController,
        configureAuth
    )
}

fun Application.mainWithOverrides(
    checklistController: ChecklistController,
    usersController: UsersController,
    adminController: AdminController,
    gameController: GameController,
    chatController: ChatController,
    configureAuth: Authentication.Configuration.() -> Unit
) {
    installCallLogging()
    install(ContentNegotiation) { jackson { defaultJackson() } }
    install(Compression) { gzip() }
    install(XForwardedHeaderSupport)
    install(HSTS)
    installCORS()
    installErrorHandling()
    install(Authentication) {
        configureAuth()
    }
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
                chatController,
            )
        }
    }
}
