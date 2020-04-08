package argent.config

import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import java.io.IOException

@Throws(IOException::class)
fun accessSecretVersion(
    projectId: String,
    secretId: String,
    versionId: String = "latest"
): String {
    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    SecretManagerServiceClient.create().use { client ->
        val name =
            SecretVersionName.of(projectId, secretId, versionId)

        // Access the secret version.
        val request =
            AccessSecretVersionRequest.newBuilder().setName(name.toString()).build()
        val response: AccessSecretVersionResponse = client.accessSecretVersion(request)

        return response.payload.data.toStringUtf8()
    }
}