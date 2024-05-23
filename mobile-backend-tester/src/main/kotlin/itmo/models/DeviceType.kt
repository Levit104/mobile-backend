package itmo.models

import kotlinx.serialization.Serializable

@Serializable
data class DeviceType(
    val id: Int,
    val name: String
)