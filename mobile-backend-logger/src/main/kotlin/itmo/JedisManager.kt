package itmo

import itmo.db.LogDataBase
import itmo.model.MessageLogDao
import kotlinx.serialization.json.Json
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub

suspend fun psqlLoggerInit(logDb: LogDataBase) {
    val jedis : Jedis = JedisPool().resource
    jedis.subscribe(object : JedisPubSub() {
        override fun onMessage(channel : String, message : String) {
            //println(Json.decodeFromString<MessageLogDao>(message))
            logDb.insertLog(Json.decodeFromString<MessageLogDao>(message))
        }
    }, "LoggerQueue")
}