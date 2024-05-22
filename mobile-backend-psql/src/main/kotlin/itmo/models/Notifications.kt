package itmo.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp

@Serializable
data class Notification(
    val id: Int?,
    val deviceId: Int,
    val userId: Int,
    val time: LocalDateTime,
    val text: String
)

object Notifications : IntIdTable("notification") {
    val deviceId = reference("device_id", Devices, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", Users)
    val time = timestamp("time")
    val text = varchar("text", 256)
}
