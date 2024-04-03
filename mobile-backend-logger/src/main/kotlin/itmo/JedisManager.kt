package itmo

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub

suspend fun psqlLoggerInit() {
    val jedis : Jedis = JedisPool().resource
    jedis.subscribe(object : JedisPubSub() {
        override fun onMessage(channel : String, message : String) {
            println(message)
        }
    }, "PSQLLogger")
}