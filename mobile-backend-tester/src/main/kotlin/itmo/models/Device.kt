package itmo.models

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: Int?,
    val name: String,
    val typeId: Int,
    val roomId: Int?,
    val userId: Int
) {
    constructor(name: String, typeId: Int, roomId: Int?, userId: Int) : this(null, name, typeId, roomId, userId)
}