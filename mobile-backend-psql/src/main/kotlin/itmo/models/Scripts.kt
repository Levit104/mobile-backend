package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Script(
    val id: Int?,
    val userId: Int,
    val deviceId: Int,
    val conditionId: Int,
    val actionId: Int,
    val conditionValue: String,
    val actionValue: String,
    val active: Boolean
)

object Scripts : IntIdTable("script") {
    val userId = reference("user_id", Users)
    val deviceId = reference("device_id", Devices)
    val conditionId = reference("condition_id", Conditions)
    val actionId = reference("action_id", Actions)
    val conditionValue = varchar("condition_value", 128)
    val actionValue = varchar("action_value", 128)
    val active = bool("active")
}
