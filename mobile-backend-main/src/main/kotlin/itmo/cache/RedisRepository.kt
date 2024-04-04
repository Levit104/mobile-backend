package itmo.cache

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

interface RedisRepository<T> {
    val jedis : Jedis
        get() = JedisPool().resource

    suspend fun addItem(name: String, item : T, time : Long = 60000)

    suspend fun getItem(name: String) : Map<String, String> {
        return jedis.hgetAll(name)
    }

    suspend fun getField(name: String, field: String) : String? {
        return getItem(name)[field]
    }

    suspend fun isItemExists(name: String) : Boolean {
        return jedis.exists(name)
    }
}