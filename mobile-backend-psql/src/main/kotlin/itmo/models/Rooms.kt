package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Room(
    val id: Int?,
    val name: String,
    val userId: Int
)

object Rooms : IntIdTable("room") {
    val name = varchar("name", 32)
    val userId = reference("user_id", Users)
}
