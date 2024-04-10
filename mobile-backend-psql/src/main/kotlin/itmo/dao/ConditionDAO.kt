package itmo.dao

import itmo.models.Condition
import itmo.models.Conditions
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class ConditionDAO : BasicDAO<Condition> {
    private fun resultRowToCondition(row: ResultRow): Condition = Condition(
        id = row[Conditions.id].value,
        description = row[Conditions.description]
    )

    override suspend fun findAll(): List<Condition> = dbQuery {
        Conditions.selectAll().map(::resultRowToCondition)
    }

    override suspend fun findById(id: Int): Condition? = dbQuery {
        Conditions.select(Conditions.id eq id).map(::resultRowToCondition).singleOrNull()
    }

    override suspend fun insert(entity: Condition): Int = dbQuery {
        Conditions.insert { it[description] = entity.description }[Conditions.id].value
    }
}
