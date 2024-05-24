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
        val con = jedis
        val map = con.hgetAll("action#$name")
        con.close()
        return ActionDTO(
            map["id"]!!.toInt(),
            map["name"]!!,
            map["deviceTypeId"]!!.toInt(),
            map["stateName"]!!,
            map["parameterMode"].toBoolean(),
        )
    }

    override suspend fun isItemExists(name: String): Boolean {
        val con = jedis
        val isExists = con.exists("action#$name")
        con.close()
        return isExists
    }

    fun addRelation(deviceTypeId: String, actionId: String, time: Long = 60_000) {
        val con = jedis
        con.sadd("deviceType_action#$deviceTypeId", actionId)
        con.pexpire("deviceType_action#$deviceTypeId", time)
        con.close()
    }

    suspend fun getItemsByDeviceTypeId(deviceTypeId: String): List<ActionDTO> {
        val con = jedis
        val set = con.smembers("deviceType_action#$deviceTypeId")
        con.close()
        return set.map { s -> this.getItem(s) }
    }

    fun isItemsExistsByDeviceTypeId(deviceTypeId: String): Boolean {
        val con = jedis
        val isExists = con.exists("deviceType_action#$deviceTypeId")
        con.close()
        return isExists
    }

    override suspend fun addItem(name: String, item: ActionDTO, time: Long) {
        val con = jedis
        con.hset("action#$name", "id", item.id.toString())
        con.hset("action#$name", "name", item.name)
        con.hset("action#$name", "deviceTypeId", item.deviceTypeId.toString())
        con.hset("action#$name", "stateName", item.stateName)
        con.hset("action#$name", "parameterMode", item.parameterMode.toString())
        con.close()
    }
}