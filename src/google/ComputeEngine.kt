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
        val result = runCatching {
            val response = RestClient.client.get(url) {
                gcpBearerAuth()
            }
            response.body<GceInstance>()
        }
        return result.getOrNull()
    }

    suspend fun startInstance() {
        RestClient.client.post("$url/start") {
            gcpBearerAuth()
        }
    }

    suspend fun stopInstance() {
        RestClient.client.post("$url/stop") {
            gcpBearerAuth()
        }
    }
}
