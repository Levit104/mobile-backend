package itmo.db.dao.statistics

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StatisticDTO (
    val id: Int,
    val deviceId: Int,
    val time: LocalDateTime,
    val waterMeter: Double,
    val electricityMeter: Double
)