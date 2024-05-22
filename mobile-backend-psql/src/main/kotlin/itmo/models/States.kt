package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

@Serializable
data class State(
    val id: Int?,
    val deviceId: Int,
    val actionId: Int,
    val value: String
) {
    constructor(deviceId: Int, actionId: Int, value: String) : this(null, deviceId, actionId, value)
}

object States : IntIdTable("state") {
    val deviceId = reference("device_id", Devices, onDelete = ReferenceOption.CASCADE)
    val actionId = reference("action_id", Actions)
    val value = varchar("value", 32)
}
