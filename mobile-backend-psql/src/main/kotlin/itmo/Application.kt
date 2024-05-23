package itmo

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import itmo.plugins.configureDatabase
import itmo.plugins.configureRouting
import itmo.plugins.configureSerialization
import itmo.util.log
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.time.Duration

fun main() {
    log("start", "-1", "Psql сервис начал свою работу", "success")
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

val poolConfig = buildPoolConfig()
var jedisPool = JedisPool(poolConfig)

private fun buildPoolConfig(): JedisPoolConfig {
    val poolConfig = JedisPoolConfig()
    poolConfig.maxTotal = 1000
    poolConfig.maxIdle = 1000
    poolConfig.minIdle = 10
    poolConfig.setMaxWait(Duration.ofSeconds(30))
    poolConfig.testOnBorrow = true
    poolConfig.testOnReturn = true
    return poolConfig
}

fun Application.module() {
    configureDatabase()
    configureRouting()
    configureSerialization()
}