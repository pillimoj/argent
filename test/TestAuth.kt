
import argent.data.users.User
import argent.data.users.UserDataStore
import argent.data.users.UserRole
import argent.util.database.DataBases
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import kotlinx.coroutines.runBlocking
import java.util.UUID

class TestAuthProvider internal constructor(
    configuration: Configuration,
) : AuthenticationProvider(configuration) {
    // internal val confValue: String = configuration.confValue
    init {
        val usersStore = UserDataStore(DataBases.Argent.dbPool)
        runBlocking {
            if (usersStore.getUserForEmail(configuration.user.email) == null) {
                usersStore.addUser(configuration.user)
            }
        }
    }

    private val user = configuration.user

    class Configuration internal constructor(name: String?) : AuthenticationProvider.Config(name) {
        lateinit var user: User
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        context.principal(user)
    }
}

fun AuthenticationConfig.testAuth(
    name: String? = null,
    configure: TestAuthProvider.Configuration.() -> Unit,
) {
    val provider = TestAuthProvider(TestAuthProvider.Configuration(name).apply(configure))
    register(provider)
}

val TestAuthDefaultUser =
    User(
        id = UUID.fromString("61585D32-69EF-4F4F-9A9B-B37EFCECE870"),
        name = "TestUserName",
        email = "test@argent.grimsborn.com",
        role = UserRole.User,
    )
