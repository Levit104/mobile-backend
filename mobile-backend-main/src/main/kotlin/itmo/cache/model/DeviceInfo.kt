package itmo.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val device: DeviceDAO,
    val actions: List<ActionDTO>,
    val states: List<StateDAO>
)