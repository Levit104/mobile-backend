package itmo.db.dao.scripts

import itmo.db.dao.actions.Actions
import itmo.db.dao.conditions.Conditions
import itmo.db.dao.devices.Devices
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Scripts : IntIdTable() {
    val deviceId = reference("device_id", Devices, onDelete = ReferenceOption.CASCADE)
    val conditionId = reference("condition_id", Conditions, onDelete = ReferenceOption.CASCADE)
    val actionId = reference("action_id", Actions, onDelete = ReferenceOption.CASCADE)
    val conditionValue = varchar("condition_value", 32)
    val actionValue = varchar("action_value", 32)
    val status = integer("status")

    fun insert(scriptDTO: ScriptDTO) {
        transaction {
            Scripts.insert {
                it[deviceId] = scriptDTO.deviceId
                it[conditionId] = scriptDTO.conditionId
                it[actionId] = scriptDTO.actionId
                it[conditionValue] = scriptDTO.conditionValue
                it[actionValue] = scriptDTO.actionValue
                it[status] = scriptDTO.status
            }
        }
    }

    fun findById(id: Int): ScriptDTO? {
        return transaction {
            try {
                val script = Scripts.select { Scripts.id.eq(id) }.single()
                ScriptDTO(
                    id = script[Scripts.id].value,
                    deviceId = script[deviceId].value,
                    conditionId = script[conditionId].value,
                    actionId = script[actionId].value,
                    conditionValue = script[conditionValue],
                    actionValue = script[actionValue],
                    status = script[status]
                )
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}