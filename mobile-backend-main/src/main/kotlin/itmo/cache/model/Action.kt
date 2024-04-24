package itmo.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class ActionDAO (
    val id: Int,
    val actionTypeId: Int,
    val deviceTypeId: Int
)