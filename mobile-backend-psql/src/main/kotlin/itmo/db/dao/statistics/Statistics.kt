package itmo.db.dao.statistics

import itmo.db.dao.devices.Devices
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.ZoneOffset

object Statistics : IntIdTable() {
    val deviceId = reference("device_id", Devices, onDelete = ReferenceOption.CASCADE)
    val time = timestamp("time")
    val waterMeter = double("water_meter")
    val electricityMeter = double("electricity_meter")

    fun insert(statisticDTO: StatisticDTO) {
        transaction {
            Statistics.insert {
                it[deviceId] = statisticDTO.deviceId
                it[time] = statisticDTO.time.toJavaLocalDateTime().toInstant(ZoneOffset.UTC)
                it[waterMeter] = statisticDTO.waterMeter
                it[electricityMeter] = statisticDTO.electricityMeter
            }
        }
    }

    fun findById(id: Int): StatisticDTO? {
        return transaction {
            try {
                val statistic = Statistics.select { Statistics.id.eq(id) }.single()
                StatisticDTO(
                    id = statistic[Statistics.id].value,
                    deviceId = statistic[Statistics.deviceId].value,
                    time = statistic[Statistics.time].toKotlinInstant().toLocalDateTime(TimeZone.UTC),
                    electricityMeter = statistic[Statistics.electricityMeter],
                    waterMeter = statistic[Statistics.waterMeter]
                )
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}