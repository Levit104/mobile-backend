package itmo.db.dao.actions

import kotlinx.serialization.Serializable

@Serializable
data class ActionDTO (
    val id: Int,
    val actionTypeId: Int,
    val deviceTypeId: Int
)