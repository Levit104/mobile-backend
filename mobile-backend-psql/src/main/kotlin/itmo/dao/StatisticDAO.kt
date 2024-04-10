package itmo.dao

import itmo.models.Statistic
import itmo.models.Statistics
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

class StatisticDAO : BasicDAO<Statistic> {
    private fun resultRowToStatistic(row: ResultRow): Statistic = Statistic(
        id = row[Statistics.id].value,
        deviceId = row[Statistics.deviceId].value,
        time = row[Statistics.time].toKotlinInstant().toLocalDateTime(TimeZone.UTC), // FIXME
        electricityMeter = row[Statistics.electricityMeter],
        waterMeter = row[Statistics.waterMeter]
    )

    override suspend fun findAll(): List<Statistic> = dbQuery {
        Statistics.selectAll().map(::resultRowToStatistic)
    }

    override suspend fun findById(id: Int): Statistic? = dbQuery {
        Statistics.select(Statistics.id eq id).map(::resultRowToStatistic).singleOrNull()
    }

    override suspend fun insert(entity: Statistic): Int = dbQuery {
        Statistics.insert {
            it[deviceId] = entity.deviceId
            it[time] = entity.time.toJavaLocalDateTime().toInstant(ZoneOffset.UTC) // FIXME
            it[waterMeter] = entity.waterMeter
            it[electricityMeter] = entity.electricityMeter
        }[Statistics.id].value
    }

    suspend fun findAllByDevice(deviceId: Int): List<Statistic> = dbQuery {
        Statistics.select(Statistics.deviceId eq deviceId).map(::resultRowToStatistic)
    }
}
