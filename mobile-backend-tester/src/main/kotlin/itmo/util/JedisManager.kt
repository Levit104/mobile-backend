package itmo.util

import itmo.jedisPool
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*


fun log(event: String, userId: String, description: String, status: String) {
    val message = MessageLogDao(
        "localhost", "test", userId, event, SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(
            Date()
        ), description, status
    )
    jedisPool.resource.publish("LoggerQueueTester", Json.encodeToString(message))
}