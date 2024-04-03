package itmo.db.dao.actions

import itmo.db.dao.actionTypes.ActionTypes
import itmo.db.dao.deviceTypes.DeviceTypes
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Actions : IntIdTable() {
    val actionTypeId = reference("action_type_id", ActionTypes, onDelete = ReferenceOption.CASCADE)
    val deviceTypeId = reference("device_type_id", DeviceTypes, onDelete = ReferenceOption.CASCADE)

    fun insert(actionDTO: ActionDTO) {
        transaction {
            Actions.insert {
                it[actionTypeId] = actionDTO.actionTypeId
                it[deviceTypeId] = actionDTO.deviceTypeId
            }
        }
    }

    fun findById(id: Int): ActionDTO? {
        return transaction {
            try {
                val action = Actions.select { Actions.id.eq(id) }.single()
                ActionDTO(
                    id = action[Actions.id].value,
                    actionTypeId = action[actionTypeId].value,
                    deviceTypeId = action[deviceTypeId].value
                )
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}