package itmo.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class StateTypeDAO (
    val id: Int,
    val name: String,
    val description: String
)