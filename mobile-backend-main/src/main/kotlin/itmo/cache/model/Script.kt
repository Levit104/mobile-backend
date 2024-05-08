package itmo.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class ScriptDao (
    val id: Int?,
    val userId: Int,
    val deviceId: Int,
    val conditionId: Int,
    val actionId: Int,
    val conditionValue: String,
    val actionValue: String,
    val active: Boolean
)