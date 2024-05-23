package itmo.cache.model

import itmo.cache.RedisRepository
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDAO(
    val id: Long?,
    val name: String,
    val typeId: Long,
    val roomId: Long?,
    val userId: Long?
)

class DeviceRedisRepository : RedisRepository<DeviceDAO, DeviceDAO> {
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

    override suspend fun getItem(deviceId: String): DeviceDAO {
        val map = jedis.hgetAll("device#$deviceId")
        return DeviceDAO(
            map["id"]!!.toLong(),
            map["name"]!!,
            map["typeId"]!!.toLong(),
            map["roomId"]?.toLong(),
            map["userId"]!!.toLong(),
        )
    }

    override suspend fun isItemExists(deviceId: String): Boolean {
        return jedis.exists("device#$deviceId")
    }

    suspend fun getItemsByUser(userId: String): List<DeviceDAO> {
        val set = jedis.smembers("user_device#$userId")
        return set.map { s -> this.getItem(s) }
    }

    fun isItemsExistsByUser(userId: String): Boolean {
        return jedis.exists("user_device$userId")
    }

    fun isItemExistsByUser(deviceId: String, username: String): Boolean {
        val userId = jedis.get("username#$username")
        return jedis.sismember("user_device#$userId", deviceId)
    }
}