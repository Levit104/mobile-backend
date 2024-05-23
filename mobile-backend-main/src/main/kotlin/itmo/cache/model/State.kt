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
        val con = jedis
        val map = con.hgetAll("state#$name")
        con.close()
        return StateDAO(
            map["id"]!!.toInt(),
            map["deviceId"]!!.toInt(),
            map["actionId"]!!.toInt(),
            map["value"]!!
        )
    }

    override suspend fun isItemExists(name: String): Boolean {
        val con = jedis
        val isExists = con.exists("state#$name")
        con.close()
        return isExists
    }

    private fun addRelation(deviceId: String, stateId: String, time: Long = 600_000) {
        val con = jedis
        con.sadd("device_state#$deviceId", stateId)
        con.pexpire("device_state#$deviceId", time)
        con.close()
    }

    suspend fun getItemsByDeviceId(deviceId: String): List<StateDAO> {
        val con = jedis
        val set = con.smembers("device_state#$deviceId")
        con.close()
        return set.map { s -> this.getItem(s) }
    }

    fun isItemsExistsByDeviceId(deviceId: String): Boolean {
        val con = jedis
        val isExists = con.exists("device_state#$deviceId")
        con.close()
        return isExists
    }

    override suspend fun addItem(name: String, item: StateDAO, time: Long) {
        val con = jedis
        con.hset("state#$name", "id", item.id.toString())
        con.hset("state#$name", "deviceId", item.deviceId.toString())
        con.hset("state#$name", "actionId", item.actionId.toString())
        con.hset("state#$name", "value", item.value)
        con.pexpire("state#$name", time)
        con.close()
        addRelation(item.deviceId.toString(), item.id.toString())
    }
}