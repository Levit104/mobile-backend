package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class DeviceType(
    val id: Int?,
    val name: String
)

object DeviceTypes : IntIdTable("device_type") {
    val name = varchar("name", 32).uniqueIndex()
}
