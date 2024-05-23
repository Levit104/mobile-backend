package itmo

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.serialization.kotlinx.json.*
import itmo.plugins.startTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig


suspend fun main() {
//    val engine = embeddedServer(Netty, port = 8085, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
    startTest()
    client.close()
    jedisPool.destroy()
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

@OptIn(ExperimentalSerializationApi::class)
val client = HttpClient(CIO) {
    engine {
        endpoint.maxConnectionsPerRoute = 100
        maxConnectionsCount = 1000
        endpoint.connectAttempts = 5
        endpoint.keepAliveTime = 1
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 30000
    }
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            explicitNulls = false
            prettyPrint = true
            isLenient = true
        })
    }
}

//fun Application.module() {
//    configureRouting()
//    configureSerialization()
//}


