package itmo.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import redis.clients.jedis.JedisPool
import java.text.SimpleDateFormat
import java.util.*


fun log(event : String, userId: String, description: String, status: String) {
    val message = MessageLogDao("localhost", "main", userId, event, SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(
        Date()
    ), description, status)
    JedisPool().resource.publish("LoggerQueue", Json.encodeToString(message))
}