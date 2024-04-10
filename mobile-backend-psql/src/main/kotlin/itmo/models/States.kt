package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class State(
    val id: Int?,
    val deviceId: Int,
    val stateTypeId: Int,
    val value: String
)

object States : IntIdTable("state") {
    val deviceId = reference("device_id", Devices)
    val stateTypeId = reference("state_type_id", StateTypes)
    val value = varchar("value", 32)
}
