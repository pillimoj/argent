package argent.server

import argent.google.accessSecretVersion
import argent.util.json
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import java.io.File

class ConfigurationError(configKey: String) : Exception("Missing configuration: $configKey")

fun getConfig(configKey: String, default: String? = null): String {
    val strValue = System.getenv(configKey)
    return strValue ?: default ?: throw ConfigurationError("Missing configuration $configKey")
}

fun getOptionalConfig(configKey: String): String? {
    val strValue = System.getenv(configKey)
    return strValue ?: null
}

fun <T> getSecretConf(serializer: DeserializationStrategy<T>, name: String): T {
    val configJsonString = accessSecretVersion(Config.googleProject, name)
    return json.parse(serializer, configJsonString)
}

fun <T> getDevConf(serializer: DeserializationStrategy<T>, name: String): T {
    val localSecretsPath = getConfig("ARGENT_DEV_LOCAL_SECRETS_PATH")
    val configJsonString = File("$localSecretsPath/$name.json").readText()
    return json.parse(serializer, configJsonString)
}

@Serializable
data class DbConf(
    val database: String,
    val host: String,
    val user: String,
    val password: String,
    val port: Int
)

object Config {

    const val port = 8008
    val debug = getConfig("ARGENT_DEBUG", "false") == "true"
    val watchPaths = if (debug) listOf("argent") else emptyList()

    val googleProject = getConfig("GOOGLE_CLOUD_PROJECT")

    val argentDb by lazy {
        if(debug) getDevConf(DbConf.serializer(), "argent-db")
        else getSecretConf(DbConf.serializer(), "argent-db")
    }
}
