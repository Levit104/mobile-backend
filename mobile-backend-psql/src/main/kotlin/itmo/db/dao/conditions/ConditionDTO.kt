package itmo.db.dao.conditions

import kotlinx.serialization.Serializable

@Serializable
data class ConditionDTO (
    val id: Int,
    val description: String
)