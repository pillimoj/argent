import argent.api.controllers.AdminController
import argent.api.controllers.ChatController
import argent.api.controllers.ChecklistController
import argent.api.controllers.GameController
import argent.api.controllers.UsersController
import argent.api.controllers.WishListController
import argent.data.chat.ChatStore
import argent.data.checklists.ChecklistDataStore
import argent.data.game.GameDatastore
import argent.data.runMigrations
import argent.data.users.User
import argent.data.users.UserDataStore
import argent.data.wishlists.WishlistDataStore
import argent.server.DataBases
import argent.server.mainWithOverrides
import io.ktor.auth.Authentication
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking

interface ApplicationContext {
    val authenticatedUser: User
    val checklistDataStore: ChecklistDataStore
    val wishlistDataStore: WishlistDataStore
    val userDataStore: UserDataStore
    val gameDataStore: GameDatastore
    val chatStore: ChatStore

    val checklistController: ChecklistController
    val wishListController: WishListController
    val adminController: AdminController
    val usersController: UsersController
    val gameController: GameController
    val chatController: ChatController
    val configureAuth: Authentication.Configuration.() -> Unit
    fun <T> testMain(callback: TestApplicationEngine.() -> T): T {
        return withTestApplication({
            mainWithOverrides(
                checklistController,
                wishListController,
                usersController,
                adminController,
                gameController,
                chatController,
                configureAuth
            )
        }) { callback() }
    }
}

fun defaultApplicationContext(authenticatedUser: User) = object : ApplicationContext {
    override val authenticatedUser = authenticatedUser
    override val checklistDataStore = ChecklistDataStore(DataBases.Argent.dbPool)
    override val wishlistDataStore = WishlistDataStore(DataBases.Argent.dbPool)
    override val userDataStore = UserDataStore(DataBases.Argent.dbPool)
    override val gameDataStore = GameDatastore(DataBases.Argent.dbPool)
    override val chatStore = ChatStore(DataBases.Argent.dbPool)
    override val checklistController = ChecklistController(checklistDataStore, userDataStore)
    override val wishListController = WishListController(wishlistDataStore)
    override val adminController = AdminController(userDataStore)
    override val usersController = UsersController(userDataStore)
    override val gameController = GameController(gameDataStore)
    override val chatController = ChatController(chatStore)
    override val configureAuth: Authentication.Configuration.() -> Unit = {
        testAuth { user = authenticatedUser }
    }

    init {
        runMigrations(DataBases.Argent.dbPool)
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
        block: suspend ApplicationContext.() -> T
    ): T {
        return runBlocking { context.run { block() } }
    }

    fun <T> testApp(
        context: ApplicationContext = defaultApplicationContext(authenticatedUser),
        block: suspend ApplicationContext.() -> T
    ) {
        runBlocking { context.run { block() } }
    }
}
