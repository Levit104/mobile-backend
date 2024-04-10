package itmo.dao

import itmo.models.DeviceType
import itmo.models.DeviceTypes
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class DeviceTypeDAO : BasicDAO<DeviceType> {
    private fun resultRowToDeviceType(row: ResultRow): DeviceType = DeviceType(
        id = row[DeviceTypes.id].value,
        name = row[DeviceTypes.name]
    )

    override suspend fun findAll(): List<DeviceType> = dbQuery {
        DeviceTypes.selectAll().map(::resultRowToDeviceType)
    }

    override suspend fun findById(id: Int): DeviceType? = dbQuery {
        DeviceTypes.select(DeviceTypes.id eq id).map(::resultRowToDeviceType).singleOrNull()
    }

    override suspend fun insert(entity: DeviceType): Int = dbQuery {
        DeviceTypes.insert { it[name] = entity.name }[DeviceTypes.id].value
    }
}
