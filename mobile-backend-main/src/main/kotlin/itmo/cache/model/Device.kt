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
        val con = jedis
        con.hset("device#$deviceId", "id", item.id.toString())
        con.hset("device#$deviceId", "name", item.name)
        con.hset("device#$deviceId", "typeId", item.typeId.toString())
        con.hset("device#$deviceId", "roomId", item.roomId.toString())
        con.hset("device#$deviceId", "userId", item.userId.toString())
        con.pexpire("device#$deviceId", time)
        con.sadd("user_device#${item.userId}", deviceId)
        con.pexpire("user_device#${item.userId}", time)
        con.close()
    }

    override suspend fun getItem(deviceId: String): DeviceDAO {
        val con = jedis
        val map = con.hgetAll("device#$deviceId")
        con.close()
        return DeviceDAO(
            map["id"]!!.toLong(),
            map["name"]!!,
            map["typeId"]!!.toLong(),
            map["roomId"]?.toLong(),
            map["userId"]!!.toLong(),
        )
    }

    override suspend fun isItemExists(deviceId: String): Boolean {
        val con = jedis
        val isExists = con.exists("device#$deviceId")
        con.close()
        return isExists
    }

    suspend fun getItemsByUser(userId: String): List<DeviceDAO> {
        val con = jedis
        val set = con.smembers("user_device#$userId")
        con.close()
        return set.map { s -> this.getItem(s) }
    }

    fun isItemsExistsByUser(userId: String): Boolean {
        val con = jedis
        val isExists = con.exists("user_device$userId")
        con.close()
        return isExists
    }

    fun isItemExistsByUser(deviceId: String, username: String): Boolean {
        val con = jedis
        val userId = con.get("username#$username")
        val isExists = con.sismember("user_device#$userId", deviceId)
        con.close()
        return isExists
    }
}