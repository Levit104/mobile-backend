package itmo.dao

import itmo.models.ActionType
import itmo.models.ActionTypes
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class ActionTypeDAO : BasicDAO<ActionType> {
    private fun resultRowToActionType(row: ResultRow): ActionType = ActionType(
        id = row[ActionTypes.id].value,
        stateTypeId = row[ActionTypes.stateTypeId].value,
        description = row[ActionTypes.description],
        parameterMode = row[ActionTypes.parameterMode]
    )

    override suspend fun findAll(): List<ActionType> = dbQuery {
        ActionTypes.selectAll().map(::resultRowToActionType)
    }

    override suspend fun findById(id: Int): ActionType? = dbQuery {
        ActionTypes.select(ActionTypes.id eq id).map(::resultRowToActionType).singleOrNull()
    }

    override suspend fun insert(entity: ActionType): Int = dbQuery {
        ActionTypes.insert {
            it[stateTypeId] = entity.stateTypeId
            it[description] = entity.description
            it[parameterMode] = entity.parameterMode
        }[ActionTypes.id].value
    }
}
