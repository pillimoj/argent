package argent.config

import argent.util.defaultObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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

inline fun <reified T> getSecretConf(name: String, debug: Boolean): T{
    val configJsonString = if(debug){
        val localSecretsPath = getConfig("ARGENT_DEV_LOCAL_SECRETS_PATH")
        File("$localSecretsPath/$name.json").readText()
    }
    else{
        accessSecretVersion(Config.googleProject, name)
    }
    return defaultObjectMapper.readValue(configJsonString)
}

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
    val watchPaths = if(debug) listOf("argent") else emptyList()

    val googleProject = getConfig("GOOGLE_CLOUD_PROJECT")


    val argentDb by lazy { getSecretConf<DbConf>("argent-db", debug) }
}
