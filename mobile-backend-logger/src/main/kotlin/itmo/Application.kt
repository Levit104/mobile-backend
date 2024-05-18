package itmo

import io.ktor.network.sockets.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import itmo.db.LogDataBase
import itmo.plugins.*
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager

lateinit var logDB: LogDataBase
fun main() {
    logDB = LogDataBase()
    logDB.initDataBase()

    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
    launch {
        psqlLoggerInit(logDB)
    }
}
