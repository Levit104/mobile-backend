package itmo.cache.model

import itmo.cache.RedisRepository
import kotlinx.serialization.Serializable

// TODO: 10.04.2024
@Serializable
data class RoomDAO(
    val id: Long?,
    val name: String,
    val userId: Long?
)

class RoomRedisRepository : RedisRepository<String, String> {
    override suspend fun addItem(roomId: String, roomName: String, time: Long) {
        val con = jedis
        con.set("room#$roomId", roomName)
        con.sadd("room", roomId)
        con.close()
    }

    override suspend fun getItem(roomId: String): String {
        val con = jedis
        val isExists = con.get("room#$roomId")
        con.close()
        return isExists
    }

    override suspend fun isItemExists(roomId: String): Boolean {
        val con = jedis
        val isExists = con.exists("room#$roomId")
        con.close()
        return isExists
    }

    fun getItems(): Map<String, String> {
        val con = jedis
        val set = con.smembers("room")
        val items = set.associateWith { s -> con.get("room#$s") }
        con.close()
        return items
    }
}