package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Condition(
    val id: Int?,
    val description: String
)

object Conditions : IntIdTable("condition") {
    val description = varchar("description", 128).uniqueIndex()
}
