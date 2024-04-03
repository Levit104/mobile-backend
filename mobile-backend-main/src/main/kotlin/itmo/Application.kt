package itmo

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import itmo.plugins.*




fun main() {
    embeddedServer(Netty, port = 8082, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    println(environment.config.keys())

    configureSecurity()
    configureRouting()
    configureSerialization()
}
