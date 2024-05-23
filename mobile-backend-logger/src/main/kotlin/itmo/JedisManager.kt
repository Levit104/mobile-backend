package itmo

import itmo.db.LogDataBase
import itmo.model.MessageLogDao
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import redis.clients.jedis.JedisPubSub

suspend fun psqlLoggerInit(logDb: LogDataBase) {
    coroutineScope {
        launch {
            jedisPool.resource.subscribe(object : JedisPubSub() {
                override fun onMessage(channel: String, message: String) {
                    println(Json.decodeFromString<MessageLogDao>(message))
                    logDb.insertLog(Json.decodeFromString<MessageLogDao>(message))
                }
            }, "LoggerQueueTester")
        }
        launch {
            jedisPool.resource.subscribe(object : JedisPubSub() {
                override fun onMessage(channel: String, message: String) {
                    println(Json.decodeFromString<MessageLogDao>(message))
                    logDb.insertLog(Json.decodeFromString<MessageLogDao>(message))
                }
            }, "LoggerQueueMain")
        }
        launch {
            jedisPool.resource.subscribe(object : JedisPubSub() {
                override fun onMessage(channel: String, message: String) {
                    println(Json.decodeFromString<MessageLogDao>(message))
                    logDb.insertLog(Json.decodeFromString<MessageLogDao>(message))
                }
            }, "LoggerQueuePsql")
        }
    }
}