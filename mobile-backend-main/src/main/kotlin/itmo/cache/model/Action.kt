package itmo.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class ActionDAO (
    val id: Int,
    val deviceTypeId: Int,
    val stateTypeId: Int,
    val name: String,
    val parameterMode: Boolean,
    /* Вырезать Action Type, обновить Action, */
)