package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Action(
    val id: Int?,
    val actionTypeId: Int,
    val deviceTypeId: Int
)

object Actions : IntIdTable("action") {
    val actionTypeId = reference("action_type_id", ActionTypes)
    val deviceTypeId = reference("device_type_id", DeviceTypes)
}
