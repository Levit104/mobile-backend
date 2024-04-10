package itmo.dao

import itmo.models.Script
import itmo.models.Scripts
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class ScriptDAO : BasicDAO<Script> {
    private fun resultRowToScript(row: ResultRow): Script = Script(
        id = row[Scripts.id].value,
        userId = row[Scripts.userId].value,
        deviceId = row[Scripts.deviceId].value,
        conditionId = row[Scripts.conditionId].value,
        actionId = row[Scripts.actionId].value,
        conditionValue = row[Scripts.conditionValue],
        actionValue = row[Scripts.actionValue],
        active = row[Scripts.active]
    )

    override suspend fun findAll(): List<Script> = dbQuery {
        Scripts.selectAll().map(::resultRowToScript)
    }

    override suspend fun findById(id: Int): Script? = dbQuery {
        Scripts.select(Scripts.id eq id).map(::resultRowToScript).singleOrNull()
    }

    override suspend fun insert(entity: Script): Int = dbQuery {
        Scripts.insert {
            it[deviceId] = entity.deviceId
            it[userId] = entity.userId
            it[conditionId] = entity.conditionId
            it[actionId] = entity.actionId
            it[conditionValue] = entity.conditionValue
            it[actionValue] = entity.actionValue
            it[active] = entity.active
        }[Scripts.id].value
    }

    suspend fun findAllByUser(userId: Int): List<Script> = dbQuery {
        Scripts.select(Scripts.userId eq userId).map(::resultRowToScript)
    }
}