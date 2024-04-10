package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Device(
    val id: Int?,
    val name: String,
    val typeId: Int,
    val roomId: Int?,
    val userId: Int
)

object Devices : IntIdTable("device") {
    val name = varchar("name", 32)
    val typeId = reference("type_id", DeviceTypes)
    val roomId = reference("room_id", Rooms).nullable()
    val userId = reference("user_id", Users)
}
