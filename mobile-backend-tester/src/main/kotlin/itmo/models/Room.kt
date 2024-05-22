package itmo.models

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val name: String,
    val userId: Int
)