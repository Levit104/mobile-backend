package itmo.models

import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val id: Int,
    val name: String,
    val deviceTypeId: Int,
    val stateName: String,
    val parameterMode: Boolean,
)