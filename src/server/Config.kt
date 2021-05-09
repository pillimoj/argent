@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused")

package argent.server

import argent.google.accessSecretVersion
import argent.util.defaultObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

class ConfigurationError(configKey: String) : Exception("Missing configuration: $configKey")

fun getConfig(configKey: String, default: String? = null): String {
    val strValue = System.getenv(configKey)
    return strValue ?: default ?: throw ConfigurationError("Missing configuration $configKey")
}

inline fun <reified T> getSecretConf(name: String): T {
    val configJsonString = accessSecretVersion(Config.googleProject, name)
    return defaultObjectMapper.readValue(configJsonString)
}

inline fun <reified T> getDevConf(name: String): T {
    val localSecretsPath = getConfig("ARGENT_DEV_LOCAL_SECRETS_PATH")
    val configJsonString = File("$localSecretsPath/$name.json").readText()
    return defaultObjectMapper.readValue(configJsonString)
}

data class AuthConf(
    val jwtKey: String,
    val secureCookie: Boolean,
    val cookieName: String,
)

object Config {

    val port = getConfig("PORT", "8080").toInt()
    private val debug = getConfig("ARGENT_DEBUG", "false") == "true"

    val googleProject = getConfig("GOOGLE_CLOUD_PROJECT")

    val authentication: AuthConf by lazy {
        if (debug) getDevConf("argent-authentication")
        else getSecretConf("argent-authentication")
    }
}
