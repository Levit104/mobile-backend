package itmo.models

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val device: Device,
    val actions: List<Action>,
    val states: List<State>
)