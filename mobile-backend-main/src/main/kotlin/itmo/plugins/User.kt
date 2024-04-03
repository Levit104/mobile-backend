package itmo.plugins

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String
)


