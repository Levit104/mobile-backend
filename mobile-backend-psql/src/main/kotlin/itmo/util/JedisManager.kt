package itmo.util

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import java.text.SimpleDateFormat
import java.util.*

val jedis : Jedis = JedisPool().resource

suspend fun log(event : String, userId: String, description: String, status: String) {
    val message = MessageLogDao("localhost", "psql", userId, event, SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(
        Date()
    ), description, status)
    jedis.publish("LoggerQueue", Json.encodeToString(message))
//    jedis.subscribe(object : JedisPubSub() {
//        override fun onMessage(channel : String, message : String) {
//            println(message)
//        }
//    }, "test")
}