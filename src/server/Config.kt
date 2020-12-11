package argent.server

import argent.google.accessSecretVersion
import argent.util.argentJson
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import java.io.File

class ConfigurationError(configKey: String) : Exception("Missing configuration: $configKey")

fun getConfig(configKey: String, default: String? = null): String {
    val strValue = System.getenv(configKey)
    return strValue ?: default ?: throw ConfigurationError("Missing configuration $configKey")
}

fun <T> getSecretConf(serializer: DeserializationStrategy<T>, name: String): T {
    val configJsonString = accessSecretVersion(Config.googleProject, name)
    return argentJson.decodeFromString(serializer, configJsonString)
}

fun <T> getDevConf(serializer: DeserializationStrategy<T>, name: String): T {
    val localSecretsPath = getConfig("ARGENT_DEV_LOCAL_SECRETS_PATH")
    val configJsonString = File("$localSecretsPath/$name.json").readText()
    return argentJson.decodeFromString(serializer, configJsonString)
}

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

object Config {

    val port = getConfig("PORT", "8080").toInt()
    private val debug = getConfig("ARGENT_DEBUG", "false") == "true"
    val watchPaths = if (debug) listOf("argent") else emptyList()

    val googleProject = getConfig("GOOGLE_CLOUD_PROJECT")

    val argentDb by lazy {
        if (debug) getDevConf(DbConf.serializer(), "argent-db")
        else getSecretConf(DbConf.serializer(), "argent-db")
    }

    val authenticatedEmails by lazy {
        if (debug) getDevConf(ListSerializer(String.serializer()), "authenticated-emails")
        else getSecretConf(ListSerializer(String.serializer()), "authenticated-emails")
    }

    val authentication by lazy {
        if (debug) getDevConf(AuthConf.serializer(), "argent-authentication")
        else getSecretConf(AuthConf.serializer(), "argent-authentication")

    }
}
