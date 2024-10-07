package argent.google

import argent.google.RestClient.gcpBearerAuth
import argent.server.Config
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.serialization.Serializable

enum class ComputeEngineStatus {
    PROVISIONING,
    STAGING,
    RUNNING,
    STOPPING,
    SUSPENDING,
    SUSPENDED,
    REPAIRING,
    TERMINATED,
}

@Serializable
data class GceInstance(val status: ComputeEngineStatus)

private object ComputeEngine {
    fun getUrl(project: String, zone: String, instance: String): String {
        return "https://compute.googleapis.com/compute/v1/projects/$project/zones/$zone/instances/$instance"
    }
}

object MyGceInstance {
    private val conf = Config.gceConf
    private val url = ComputeEngine.getUrl(conf.project, conf.zone, conf.instance)
    suspend fun getStatus(): GceInstance? {
        return RestClient.createClient().use {
            it.get(url) {
                gcpBearerAuth()
            }.body<GceInstance>()
        }
    }

    suspend fun startInstance() {
        RestClient.createClient().use {
            it.post("$url/start") {
                gcpBearerAuth()
            }
        }
    }

    suspend fun stopInstance() {
        RestClient.createClient().use {
            it.post("$url/stop") {
                gcpBearerAuth()
            }
        }
    }
}
