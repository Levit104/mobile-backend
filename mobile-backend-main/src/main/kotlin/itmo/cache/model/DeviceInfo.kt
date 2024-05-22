package itmo.cache.model

data class DeviceInfo (
    val device: DeviceDAO,
    val actions: List<ActionDTO>,
    val states: List<StateDAO>
        )