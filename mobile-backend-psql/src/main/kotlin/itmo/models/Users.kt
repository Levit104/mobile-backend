package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class User(
    val id: Int?,
    val login: String,
    val password: String
)

object Users : IntIdTable("users") {
    val login = varchar("login", 32).uniqueIndex()
    val password = varchar("password", 32)
}
