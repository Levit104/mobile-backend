package itmo.db.dao.stateType

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object StateTypes : IntIdTable() {
    val name = varchar("name", 32).uniqueIndex()
    val description = varchar("description", 128).uniqueIndex()

    fun insert(stateTypeDTO: StateTypeDTO) {
        transaction {
            StateTypes.insert {
                it[name] = stateTypeDTO.name
                it[description] = stateTypeDTO.description
            }
        }
    }

    fun findById(id: Int): StateTypeDTO? {
        return transaction {
            try {
                val stateType = StateTypes.select { StateTypes.id.eq(id) }.single()
                StateTypeDTO(
                    id = stateType[StateTypes.id].value,
                    name = stateType[name],
                    description = stateType[description]
                )
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}