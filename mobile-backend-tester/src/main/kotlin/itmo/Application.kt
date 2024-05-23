package itmo

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.serialization.kotlinx.json.*
import itmo.plugins.startTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

suspend fun main() {
//    val engine = embeddedServer(Netty, port = 8085, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
    startTest()
    client.close()
}

//val logger = LoggerFactory.getLogger("my")

@OptIn(ExperimentalSerializationApi::class)
val client = HttpClient(CIO) {
    engine {
        endpoint.maxConnectionsPerRoute = 250
        maxConnectionsCount = 10000
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


