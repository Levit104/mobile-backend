package itmo.db.dao.actionTypes

import kotlinx.serialization.Serializable

@Serializable
data class ActionTypeDTO (
    val id: Int,
    val stateTypeId: Int,
    val description: String,
    val parameterMode: Boolean
)