package itmo.dao

import itmo.models.Room
import itmo.models.Rooms
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class RoomDAO : BasicDAO<Room> {
    private fun resultRowToRoom(row: ResultRow): Room = Room(
        id = row[Rooms.id].value,
        name = row[Rooms.name],
        userId = row[Rooms.userId].value
    )

    override suspend fun findAll(): List<Room> = dbQuery {
        Rooms.selectAll().map(::resultRowToRoom)
    }

    override suspend fun findById(id: Int): Room? = dbQuery {
        Rooms.select(Rooms.id eq id).map(::resultRowToRoom).singleOrNull()
    }

    override suspend fun insert(entity: Room): Int = dbQuery {
        Rooms.insert {
            it[name] = entity.name
            it[userId] = entity.userId
        }[Rooms.id].value
    }

    suspend fun findAllByUser(userId: Int): List<Room> = dbQuery {
        Rooms.select(Rooms.userId eq userId).map(::resultRowToRoom)
    }
}
