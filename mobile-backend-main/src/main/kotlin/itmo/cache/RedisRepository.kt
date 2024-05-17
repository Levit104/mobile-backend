package itmo.cache

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPooled

interface RedisRepository<T, V> {
    val jedis : Jedis
        get() = Jedis()

    suspend fun addItem(name: String, item : T, time : Long = 60000)

    suspend fun getItem(name: String) : V

    suspend fun isItemExists(name: String) : Boolean
}