package itmo.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

@Serializable
data class Statistic(
    val id: Int?,
    val deviceId: Int,
    val time: LocalDateTime,
    val waterMeter: Double?,
    val electricityMeter: Double?
)

object Statistics : IntIdTable("statistic") {
    val deviceId = reference("device_id", Devices).uniqueIndex()
    val time = timestamp("time")
    val waterMeter = double("water_meter").nullable()
    val electricityMeter = double("electricity_meter").nullable()
}
