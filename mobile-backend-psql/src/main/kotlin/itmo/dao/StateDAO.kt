package itmo.dao

import itmo.models.State
import itmo.models.States
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class StateDAO : BasicDAO<State> {
    private fun resultRowToState(row: ResultRow): State = State(
        id = row[States.id].value,
        deviceId = row[States.deviceId].value,
        actionId = row[States.actionId].value,
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
            it[actionId] = entity.actionId
            it[value] = entity.value
        }[States.id].value
    }

    suspend fun findAllByDevice(deviceId: Int): List<State> = dbQuery {
        States.select(States.deviceId eq deviceId).map(::resultRowToState)
    }

    suspend fun updateValueByDeviceIdAndActionId(deviceId: Int, actionId: Int, parameter: String): Int = dbQuery {
        States.update({
            (States.deviceId eq deviceId) and (States.actionId eq actionId)
        }) {
            it[value] = parameter
        }
    }
}
