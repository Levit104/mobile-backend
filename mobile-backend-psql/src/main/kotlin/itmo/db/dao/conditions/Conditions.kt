package itmo.db.dao.conditions

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Conditions : IntIdTable() {
    val description = varchar("description", 128)

    fun insert(conditionDTO: ConditionDTO) {
        transaction {
            Conditions.insert {
                it[description] = conditionDTO.description
            }
        }
    }

    fun findById(id: Int): ConditionDTO? {
        return transaction {
            try {
                val condition = Conditions.select { Conditions.id.eq(id) }.single()
                ConditionDTO(
                    id = condition[Conditions.id].value,
                    description = condition[description]
                )
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}