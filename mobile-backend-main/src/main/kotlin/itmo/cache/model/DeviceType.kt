package itmo.cache.model

import itmo.cache.RedisRepository
import kotlinx.serialization.Serializable

@Serializable
data class DeviceTypeDAO(
    val id: Int,
    val name: String
)
class DeviceTypeRedisRepository : RedisRepository<String, String> {
    override suspend fun addItem(typeId: String, typeName: String, time: Long) {
        jedis.set("device_type#$typeId", typeName)
        jedis.sadd("device_types", typeId)
    }

    override suspend fun getItem(typeId: String): String {
        return jedis.get("device_type#$typeId")
    }

    override suspend fun isItemExists(typeId: String): Boolean {
        return jedis.exists("device_type#$typeId")
    }

    suspend fun getItems() : Map<String, String> {
        val set = jedis.smembers("device_types")
        return set.associateWith { s -> jedis.get("device_type#$s") }
    }
}