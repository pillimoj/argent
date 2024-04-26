package argent.server

import argent.data.users.User
import argent.data.users.UserDataStore
import argent.google.accessSecretVersion
import argent.util.argentJson
import argent.util.database.DataBases
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import java.io.File

class ConfigurationError(configKey: String) : Exception("Missing configuration: $configKey")

@Serializable
data class DbConf(
    val database: String,
    val user: String,
    val password: String,
    val tcpConf: TCPDbConf? = null,
    val cloudSqlDbConf: CloudSqlDbConf? = null
)

@Serializable
data class CloudSqlDbConf(
    val connectionName: String
)

@Serializable
data class TCPDbConf(
    val host: String,
    val port: Int
)

@Serializable
data class AuthConf(
    val jwtKey: String,
    val secureCookie: Boolean,
    val cookieName: String,
)

fun getConfig(configKey: String, default: String? = null): String {
    val strValue = System.getenv(configKey)
    return strValue ?: default ?: throw ConfigurationError("Missing configuration $configKey")
}

inline fun <reified T> getSecretConf(name: String, serializer: DeserializationStrategy<T>): T {
    val configJsonString = accessSecretVersion(Config.googleProject, name)
    return argentJson.decodeFromString(serializer, configJsonString)
}

inline fun <reified T> getDevConf(name: String, serializer: DeserializationStrategy<T>): T {
    val localSecretsPath = getConfig("ARGENT_DEV_LOCAL_SECRETS_PATH")
    val configJsonString = File("$localSecretsPath/$name.json").readText()
    return argentJson.decodeFromString(serializer, configJsonString)
}

object Config {
    val port = getConfig("PORT", "8080").toInt()
    private val debug = getConfig("ARGENT_DEBUG", "false") == "true"

    val googleProject = getConfig("GOOGLE_CLOUD_PROJECT", "")

    val authentication: AuthConf by lazy {
        if (debug) getDevConf("argent-authentication", AuthConf.serializer())
        else getSecretConf("argent-authentication", AuthConf.serializer())
    }

    val argentDb: DbConf by lazy {
        if (debug) getDevConf("argent-db", DbConf.serializer())
        else getSecretConf("argent-db", DbConf.serializer())
    }

    fun initDevAdmin() {
        if (!debug) return

        val devAdminUser = getDevConf("dev-admin", User.serializer())
        val userDataStore = UserDataStore(DataBases.Argent.dbPool)
        runBlocking {
            if (userDataStore.getUser(devAdminUser.id) == null) {
                userDataStore.addUser(devAdminUser)
            }
        }
    }
}
