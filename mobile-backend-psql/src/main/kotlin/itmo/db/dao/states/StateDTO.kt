package itmo.db.dao.states

import kotlinx.serialization.Serializable

@Serializable
data class StateDTO(
    val id: Int,
    val deviceId: Int,
    val stateTypeId: Int,
    val value: String
)