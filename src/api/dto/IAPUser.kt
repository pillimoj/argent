package argent.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class IAPUser(val email: String, val id: String)
