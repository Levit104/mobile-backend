package itmo

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import itmo.db.LogDataBase
import itmo.db.schedulerInit
import itmo.plugins.*
import kotlinx.coroutines.launch

lateinit var logDB: LogDataBase
fun main() {
    logDB = LogDataBase()
    logDB.initDataBase()

    schedulerInit(logDB)

    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    launch {
        psqlLoggerInit(logDB)
    }
}
