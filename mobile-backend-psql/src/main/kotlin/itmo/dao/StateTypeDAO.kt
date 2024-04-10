package itmo.dao

import itmo.models.StateType
import itmo.models.StateTypes
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class StateTypeDAO : BasicDAO<StateType> {
    private fun resultRowToStateType(row: ResultRow): StateType = StateType(
        id = row[StateTypes.id].value,
        name = row[StateTypes.name],
        description = row[StateTypes.description]
    )

    override suspend fun findAll(): List<StateType> = dbQuery {
        StateTypes.selectAll().map(::resultRowToStateType)
    }

    override suspend fun findById(id: Int): StateType? = dbQuery {
        StateTypes.select(StateTypes.id eq id).map(::resultRowToStateType).singleOrNull()
    }

    override suspend fun insert(entity: StateType): Int = dbQuery {
        StateTypes.insert {
            it[name] = entity.name
            it[description] = entity.description
        }[StateTypes.id].value
    }
}
