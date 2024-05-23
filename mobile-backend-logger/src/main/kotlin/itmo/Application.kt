package itmo

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import itmo.db.LogDataBase
import itmo.db.schedulerInit
import itmo.plugins.*
import kotlinx.coroutines.launch
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

lateinit var logDB: LogDataBase
fun main() {
    logDB = LogDataBase()
    logDB.initDataBase()

    schedulerInit(logDB)

    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

val poolConfig = buildPoolConfig()
var jedisPool = JedisPool(poolConfig)

private fun buildPoolConfig(): JedisPoolConfig {
    val poolConfig = JedisPoolConfig()
    poolConfig.maxTotal = 128
    poolConfig.maxIdle = 128
    poolConfig.minIdle = 16
    poolConfig.testOnBorrow = true
    poolConfig.testOnReturn = true
    poolConfig.testWhileIdle = true
    poolConfig.numTestsPerEvictionRun = 3
    poolConfig.blockWhenExhausted = true
    return poolConfig
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    launch {
        psqlLoggerInit(logDB)
    }
}
