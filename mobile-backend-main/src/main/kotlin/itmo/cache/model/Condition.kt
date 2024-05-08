package itmo.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class ConditionDAO (
    val id: Int?,
    val description: String
)