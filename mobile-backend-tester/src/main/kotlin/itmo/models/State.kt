package itmo.models

import kotlinx.serialization.Serializable

@Serializable
data class State(
    val id: Int,
    val deviceId: Int,
    val actionId: Int,
    val value: String
)