
import argent.data.users.User
import argent.data.users.UserDataStore
import argent.data.users.UserRole
import argent.util.database.DataBases
import io.ktor.auth.Authentication
import io.ktor.auth.AuthenticationPipeline
import io.ktor.auth.AuthenticationProvider
import kotlinx.coroutines.runBlocking
import java.util.UUID

class TestAuthProvider internal constructor(
    configuration: Configuration
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

    val user = configuration.user

    class Configuration internal constructor(name: String?) : AuthenticationProvider.Configuration(name) {
        lateinit var user: User
    }
}

fun Authentication.Configuration.testAuth(
    name: String? = null,
    configure: TestAuthProvider.Configuration.() -> Unit
) {
    val provider = TestAuthProvider(TestAuthProvider.Configuration(name).apply(configure))
    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        context.principal(provider.user)
        return@intercept
    }
    register(provider)
}

val TestAuthDefaultUser = User(
    user = UUID.fromString("61585D32-69EF-4F4F-9A9B-B37EFCECE870"),
    name = "TestUserName",
    email = "test@argent.grimsborn.com",
    role = UserRole.User,
)
