package itmo.cache

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

interface RedisRepository<T, V> {
    val jedis : Jedis
        get() = JedisPool().resource

    suspend fun addItem(name: String, item : T, time : Long = 60000)

    suspend fun getItem(name: String) : V

    suspend fun isItemExists(name: String) : Boolean
}