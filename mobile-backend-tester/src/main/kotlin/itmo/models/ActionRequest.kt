package itmo.models

import kotlinx.serialization.Serializable

@Serializable
data class ActionRequest(
    val id: Int,
    val deviceId: Int,
    val parameter: String,
)