package itmo.cache

import redis.clients.jedis.Jedis

interface RedisRepository<T, V> {
    val jedis: Jedis
        get() = Jedis()

    suspend fun addItem(name: String, item: T, time: Long = 600_000)

    suspend fun getItem(name: String): V

    suspend fun isItemExists(name: String): Boolean
}