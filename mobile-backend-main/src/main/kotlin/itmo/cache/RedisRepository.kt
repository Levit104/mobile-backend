package itmo.cache

import itmo.jedisPool
import redis.clients.jedis.Jedis

interface RedisRepository<T, V> {
    val jedis: Jedis
        get() = jedisPool.resource

    suspend fun addItem(name: String, item: T, time: Long = 60_000)

    suspend fun getItem(name: String): V

    suspend fun isItemExists(name: String): Boolean
}