package itmo.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class StateDAO(
    val id: Int,
    val deviceId: Int,
    val stateTypeId: Int,
    val value: String
)