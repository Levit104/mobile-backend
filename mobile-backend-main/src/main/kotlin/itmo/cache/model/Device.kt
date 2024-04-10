package itmo.cache.model

import itmo.cache.RedisRepository
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDAO (
    val id : Long,
    val name : String,
    val typeId : Long,
    val roomId : Long,
    val userId : Long
    )

class DeviceRedisRepository : RedisRepository<DeviceDAO, Map<String, String>> {
    override suspend fun addItem(deviceId: String, item: DeviceDAO, time: Long) {
        jedis.hset("device#$deviceId", "id", item.id.toString())
        jedis.hset("device#$deviceId", "name", item.name)
        jedis.hset("device#$deviceId", "typeId", item.typeId.toString())
        jedis.hset("device#$deviceId", "roomId", item.roomId.toString())
        jedis.hset("device#$deviceId", "userId", item.userId.toString())
        jedis.pexpire("device#$deviceId", time)
        jedis.sadd("user_device#${item.userId}", deviceId)
        jedis.pexpire("user_device#${item.userId}", time)
    }

    override suspend fun getItem(deviceId : String): Map<String, String> {
        return jedis.hgetAll("device#$deviceId")
    }

    override suspend fun isItemExists(deviceId : String): Boolean {
        return jedis.exists("device#$deviceId")
    }

    suspend fun getItemsByUser(userId: String) : List<Map<String, String>> {
        val set = jedis.smembers("user_device#$userId")
        return set.map { s -> this.getItem(s) }
    }

    suspend fun isItemsExistsByUser(userId: String) : Boolean{
        return jedis.exists("user_device$userId")
    }

    suspend fun isItemExistsByUser(deviceId: String, username : String) : Boolean {
        val userId = jedis.get("username#$username")
        return jedis.sismember("user_device#$userId", deviceId)
    }
}