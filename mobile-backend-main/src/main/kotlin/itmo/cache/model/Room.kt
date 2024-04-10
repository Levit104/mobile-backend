package itmo.cache.model

import itmo.cache.RedisRepository

// TODO: 10.04.2024  
class RoomRedisRepository : RedisRepository<String, String> {
    override suspend fun addItem(roomId: String, roomName: String, time: Long) {
        jedis.set("room#$roomId", roomName)
        jedis.sadd("room", roomId)
    }

    override suspend fun getItem(roomId: String): String {
        return jedis.get("room#$roomId")
    }

    override suspend fun isItemExists(roomId: String): Boolean {
        return jedis.exists("room#$roomId")
    }

    suspend fun getItems() : Map<String, String> {
        val set = jedis.smembers("room")
        return set.associateWith { s -> jedis.get("room#$s") }
    }
}