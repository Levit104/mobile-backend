package itmo.cache.model

import itmo.cache.RedisRepository
import kotlinx.serialization.Serializable

@Serializable
data class DeviceTypeDAO(
    val id: Int?,
    val name: String
)

class DeviceTypeRedisRepository : RedisRepository<String, String> {
    override suspend fun addItem(typeId: String, typeName: String, time: Long) {
        val con = jedis
        con.set("device_type#$typeId", typeName)
        con.sadd("device_types", typeId)
        con.close()
    }

    override suspend fun getItem(typeId: String): String {
        val con = jedis
        val isExists = con.get("device_type#$typeId")
        con.close()
        return isExists
    }

    override suspend fun isItemExists(typeId: String): Boolean {
        val con = jedis
        val isExists = con.exists("device_type#$typeId")
        con.close()
        return isExists
    }

    fun getItems(): List<DeviceTypeDAO> {
        val con = jedis
        val set = con.smembers("device_types")
        val types = ArrayList<DeviceTypeDAO>()
        set.forEach { s ->
            types.add(DeviceTypeDAO(s.toInt(), con.get("device_type#$s")))
        }
        con.close()
        return types
    }
}