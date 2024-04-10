package itmo.dao

import itmo.models.Notification
import itmo.models.Notifications
import itmo.util.dbQuery
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.ZoneOffset

class NotificationDAO : BasicDAO<Notification> {
    private fun resultRowToNotification(row: ResultRow): Notification = Notification(
        id = row[Notifications.id].value,
        deviceId = row[Notifications.deviceId].value,
        userId = row[Notifications.userId].value,
        text = row[Notifications.text],
        time = row[Notifications.time].toKotlinInstant().toLocalDateTime(TimeZone.UTC) // FIXME
    )

    override suspend fun findAll(): List<Notification> = dbQuery {
        Notifications.selectAll().map(::resultRowToNotification)
    }

    override suspend fun findById(id: Int): Notification? = dbQuery {
        Notifications.select(Notifications.id eq id).map(::resultRowToNotification).singleOrNull()
    }

    override suspend fun insert(entity: Notification): Int = dbQuery {
        Notifications.insert {
            it[deviceId] = entity.deviceId
            it[userId] = entity.userId
            it[time] = entity.time.toJavaLocalDateTime().toInstant(ZoneOffset.UTC) // FIXME
            it[text] = entity.text
        }[Notifications.id].value
    }

    suspend fun findAllByUser(userId: Int): List<Notification> = dbQuery {
        Notifications.select(Notifications.userId eq userId).map(::resultRowToNotification)
    }
}
