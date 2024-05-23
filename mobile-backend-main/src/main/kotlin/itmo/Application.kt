package itmo

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import itmo.plugins.*
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig


fun main() {
    embeddedServer(Netty, port = 8082, host = "0.0.0.0", module = Application::module)
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
    println(environment.config.keys())

    configureSecurity()
    configureRouting()
    configureSerialization()
}
