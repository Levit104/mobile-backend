package itmo.cache.model

import itmo.cache.RedisRepository
import kotlinx.serialization.Serializable

@Serializable
data class ActionDAO(
    val id: Int,
    val deviceId: Int,
    val parameter: String,
)

@Serializable
data class ActionDTO(
    val id: Int,
    val name: String,
    val deviceTypeId: Int,
    val stateName: String,
    val parameterMode: Boolean,
)

class ActionRedisRepository : RedisRepository<ActionDTO, ActionDTO> {
    override suspend fun getItem(name: String): ActionDTO {
        val map = jedis.hgetAll("action#$name")
        return ActionDTO(
            map["id"]!!.toInt(),
            map["name"]!!,
            map["deviceTypeId"]!!.toInt(),
            map["stateName"]!!,
            map["parameterMode"].toBoolean(),
        )
    }

    override suspend fun isItemExists(name: String): Boolean {
        return jedis.exists("action#$name")
    }

    fun addRelation(deviceTypeId: String, actionId: String, time: Long = 60000) {
        jedis.sadd("deviceType_action#$deviceTypeId", actionId)
        jedis.pexpire("deviceType_action#$deviceTypeId", time)
    }

    suspend fun getItemsByDeviceTypeId(deviceTypeId: String): List<ActionDTO> {
        val set = jedis.smembers("deviceType_action#$deviceTypeId")
        return set.map { s -> this.getItem(s) }
    }

    fun isItemsExistsByDeviceTypeId(deviceTypeId: String): Boolean {
        return jedis.exists("deviceType_action#$deviceTypeId")
    }

    override suspend fun addItem(name: String, item: ActionDTO, time: Long) {
        jedis.hset("action#$name", "id", item.id.toString())
        jedis.hset("action#$name", "name", item.name)
        jedis.hset("action#$name", "deviceTypeId", item.deviceTypeId.toString())
        jedis.hset("action#$name", "stateName", item.stateName)
        jedis.hset("action#$name", "parameterMode", item.parameterMode.toString())
    }
}