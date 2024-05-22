package itmo.cache.model

import itmo.cache.RedisRepository
import kotlinx.serialization.Serializable

@Serializable
data class StateDAO(
    val id: Int,
    val deviceId: Int,
    val actionId: Int,
    val value: String
)

class StateRedisRepository : RedisRepository<StateDAO, StateDAO> {
    override suspend fun getItem(name: String): StateDAO {
        val map = jedis.hgetAll("state#$name")
        return StateDAO(
            map["id"]!!.toInt(),
            map["deviceId"]!!.toInt(),
            map["actionId"]!!.toInt(),
            map["value"]!!
        )
    }

    override suspend fun isItemExists(name: String): Boolean {
        return jedis.exists("state#$name")
    }

    private fun addRelation(deviceId: String, stateId: String, time: Long = 60000) {
        jedis.sadd("device_state#$deviceId", stateId)
        jedis.pexpire("device_state#$deviceId", time)
    }

    suspend fun getItemsByDeviceId(deviceId: String): List<StateDAO> {
        val set = jedis.smembers("device_state#$deviceId")
        return set.map { s -> this.getItem(s) }
    }

    fun isItemsExistsByDeviceId(deviceId: String): Boolean {
        return jedis.exists("device_state#$deviceId")
    }

    override suspend fun addItem(name: String, item: StateDAO, time: Long) {
        jedis.hset("state#$name", "id", item.id.toString())
        jedis.hset("state#$name", "deviceId", item.deviceId.toString())
        jedis.hset("state#$name", "actionId", item.actionId.toString())
        jedis.hset("state#$name", "value", item.value)
        jedis.pexpire("state#$name", time)
        addRelation(item.deviceId.toString(), item.id.toString())
    }
}