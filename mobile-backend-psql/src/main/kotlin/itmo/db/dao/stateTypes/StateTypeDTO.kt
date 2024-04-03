package itmo.db.dao.stateType

import kotlinx.serialization.Serializable

@Serializable
data class StateTypeDTO(
    val id: Int,
    val name: String,
    val description: String
)