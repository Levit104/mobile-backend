package itmo.db.dao.states

import itmo.db.dao.devices.Devices
import itmo.db.dao.stateType.StateTypes
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object States : IntIdTable() {
    val deviceId = reference("device_id", Devices, onDelete = ReferenceOption.CASCADE)
    val stateTypeId = reference("state_type_id", StateTypes, onDelete = ReferenceOption.CASCADE)
    val value = varchar("value", 32)

    fun insert(stateDTO: StateDTO) {
        transaction {
            States.insert {
                it[deviceId] = stateDTO.deviceId
                it[stateTypeId] = stateDTO.stateTypeId
                it[value] = stateDTO.value
            }
        }
    }

    fun findById(id: Int): StateDTO? {
        return transaction {
            try {
                val state = States.select { States.id.eq(id) }.single()
                StateDTO(
                    id = state[States.id].value,
                    deviceId = state[deviceId].value,
                    stateTypeId = state[stateTypeId].value,
                    value = state[value]
                )
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}