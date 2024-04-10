package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class StateType(
    val id: Int?,
    val name: String,
    val description: String
)

object StateTypes : IntIdTable("state_type") {
    val name = varchar("name", 32).uniqueIndex()
    val description = varchar("description", 128).uniqueIndex()
}
