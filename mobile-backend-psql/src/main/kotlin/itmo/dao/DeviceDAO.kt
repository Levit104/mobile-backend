package itmo.dao

import itmo.models.Device
import itmo.models.Devices
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DeviceDAO : BasicDAO<Device> {
    private fun resultRowToDevice(row: ResultRow): Device = Device(
        id = row[Devices.id].value,
        name = row[Devices.name],
        typeId = row[Devices.typeId].value,
        roomId = row[Devices.roomId]?.value,
        userId = row[Devices.userId].value
    )

    override suspend fun findAll(): List<Device> = dbQuery {
        Devices.selectAll().map(::resultRowToDevice)
    }

    override suspend fun findById(id: Int): Device? = dbQuery {
        Devices.select(Devices.id eq id).map(::resultRowToDevice).singleOrNull()
    }

    override suspend fun insert(entity: Device): Int = dbQuery {
        Devices.insert {
            it[typeId] = entity.typeId
            it[name] = entity.name
            it[roomId] = entity.roomId
            it[userId] = entity.userId
        }[Devices.id].value
    }

    suspend fun findAllByUser(userId: Int): List<Device> = dbQuery {
        Devices.select(Devices.userId eq userId).map(::resultRowToDevice)
    }

    suspend fun deleteById(id: Int) = dbQuery {
        Devices.deleteWhere { Devices.id eq id }
    }
}
