package itmo.db.dao.scripts

import kotlinx.serialization.Serializable

@Serializable
data class ScriptDTO (
    val id: Int,
    val deviceId: Int,
    val conditionId: Int,
    val actionId: Int,
    val conditionValue: String,
    val actionValue: String,
    val status: Int
)