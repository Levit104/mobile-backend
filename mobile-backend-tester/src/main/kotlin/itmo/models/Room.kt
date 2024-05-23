package itmo.models

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val id: Int?,
    val name: String,
    val userId: Int
) {
    constructor(name: String, userId: Int) : this(null, name, userId)
}