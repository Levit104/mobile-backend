package itmo.db.dao.actionTypes

import itmo.db.dao.stateType.StateTypes
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object ActionTypes : IntIdTable() {
    val stateTypeId = reference("state_type_id", StateTypes, onDelete = ReferenceOption.CASCADE)
    val description = varchar("description", 128)
    val parameterMode = bool("parameter_mode")

    fun insert(actionTypeDTO: ActionTypeDTO) {
        transaction {
            ActionTypes.insert {
                it[stateTypeId] = actionTypeDTO.stateTypeId
                it[description] = actionTypeDTO.description
                it[parameterMode] = actionTypeDTO.parameterMode
            }
        }
    }

    fun findById(id: Int): ActionTypeDTO? {
        return transaction {
            try {
                val actionType = ActionTypes.select { ActionTypes.id.eq(id) }.single()
                ActionTypeDTO(
                    id = actionType[ActionTypes.id].value,
                    stateTypeId = actionType[stateTypeId].value,
                    description = actionType[description],
                    parameterMode = actionType[parameterMode]
                )
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}