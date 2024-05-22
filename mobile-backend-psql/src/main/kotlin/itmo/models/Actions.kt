package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Action(
    val id: Int?,
    val name: String,
    val deviceTypeId: Int,
    val stateTypeId: Int,
    val parameterMode: Boolean,
)

object Actions : IntIdTable("action") {
    val name = varchar("name", 128)
    val deviceTypeId = reference("device_type_id", DeviceTypes)
    val stateTypeId = reference("state_type_id", StateTypes)
    val parameterMode = bool("parameter_mode")
}
