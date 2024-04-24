package itmo.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class ActionTypeDAO (
    val id: Int?,
    val stateTypeId: Int,
    val description: String,
    val parameterMode: Boolean
)