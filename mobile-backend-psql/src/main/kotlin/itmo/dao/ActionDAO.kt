package itmo.dao

import itmo.models.Action
import itmo.models.Actions
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class ActionDAO : BasicDAO<Action> {
    private fun resultRowToAction(row: ResultRow): Action = Action(
        id = row[Actions.id].value,
        name = row[Actions.name],
        deviceTypeId = row[Actions.deviceTypeId].value,
        stateTypeId = row[Actions.stateTypeId].value,
        parameterMode = row[Actions.parameterMode]
    )

    override suspend fun findAll(): List<Action> = dbQuery {
        Actions.selectAll().map(::resultRowToAction)
    }

    override suspend fun findById(id: Int): Action? = dbQuery {
        Actions.select(Actions.id eq id).map(::resultRowToAction).singleOrNull()
    }

    override suspend fun insert(entity: Action): Int = dbQuery {
        Actions.insert {
            it[name] = entity.name
            it[deviceTypeId] = entity.deviceTypeId
            it[stateTypeId] = entity.stateTypeId
            it[parameterMode] = entity.parameterMode
        }[Actions.id].value
    }

    suspend fun findAllByDeviceType(deviceTypeId: Int): List<Action> = dbQuery {
        Actions.select(Actions.deviceTypeId eq deviceTypeId).map(::resultRowToAction)
    }
}
