import argent.api.controllers.AdminController
import argent.api.controllers.ChecklistController
import argent.api.controllers.GameController
import argent.api.controllers.UsersController
import argent.data.checklists.ChecklistDataStore
import argent.data.game.GameDatastore
import argent.data.users.User
import argent.data.users.UserDataStore
import argent.server.mainWithOverrides
import argent.util.database.DataBases
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking

interface ApplicationContext {
    val authenticatedUser: User
    val checklistDataStore: ChecklistDataStore
    val userDataStore: UserDataStore
    val gameDataStore: GameDatastore

    val checklistController: ChecklistController
    val adminController: AdminController
    val usersController: UsersController
    val gameController: GameController
    val configureAuth: AuthenticationConfig.() -> Unit

    fun <T> testMain(callback: ApplicationTestBuilder.() -> T): T {
        var result: T? = null
        testApplication {
            application {
                mainWithOverrides(
                    checklistController,
                    usersController,
                    adminController,
                    gameController,
                    configureAuth,
                )
            }
            result = callback()
        }
        return result ?: throw Exception("Test did not produce a result")
    }
}

fun defaultApplicationContext(authenticatedUser: User) =
    object : ApplicationContext {
        private val db = DataBases.Argent.dbPool
        override val authenticatedUser = authenticatedUser
        override val checklistDataStore = ChecklistDataStore(db)
        override val userDataStore = UserDataStore(db)
        override val gameDataStore = GameDatastore(db)

        override val checklistController = ChecklistController(checklistDataStore, userDataStore)
        override val adminController = AdminController(userDataStore)
        override val usersController = UsersController(userDataStore)
        override val gameController = GameController(gameDataStore)
        override val configureAuth: AuthenticationConfig.() -> Unit = {
            testAuth { user = authenticatedUser }
        }

        init {
            runBlocking {
                if (null == userDataStore.getUserForEmail(authenticatedUser.email)) {
                    userDataStore.addUser(authenticatedUser)
                }
            }
        }
    }

interface ApplicationTest {
    val authenticatedUser: User

    fun <T : Any> withAppContext(
        context: ApplicationContext = defaultApplicationContext(authenticatedUser),
        block: suspend ApplicationContext.() -> T,
    ): T {
        return runBlocking { context.run { block() } }
    }

    fun <T> testApp(
        context: ApplicationContext = defaultApplicationContext(authenticatedUser),
        block: suspend ApplicationContext.() -> T,
    ) {
        runBlocking { context.run { block() } }
    }
}
