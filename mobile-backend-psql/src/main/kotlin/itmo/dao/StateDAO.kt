package itmo.dao

import itmo.models.State
import itmo.models.States
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class StateDAO : BasicDAO<State> {
    private fun resultRowToState(row: ResultRow): State = State(
        id = row[States.id].value,
        deviceId = row[States.deviceId].value,
        stateTypeId = row[States.stateTypeId].value,
        value = row[States.value]
    )

    override suspend fun findAll(): List<State> = dbQuery {
        States.selectAll().map(::resultRowToState)
    }

    override suspend fun findById(id: Int): State? = dbQuery {
        States.select(States.id eq id).map(::resultRowToState).singleOrNull()
    }

    override suspend fun insert(entity: State): Int = dbQuery {
        States.insert {
            it[deviceId] = entity.deviceId
            it[stateTypeId] = entity.stateTypeId
            it[value] = entity.value
        }[States.id].value
    }

    suspend fun findAllByDevice(deviceId: Int): List<State> = dbQuery {
        States.select(States.deviceId eq deviceId).map(::resultRowToState)
    }
}
