package itmo.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import itmo.routes.*

fun Application.configureRouting() {
    routing {
        userRouting()
        roomRouting()
        deviceTypeRouting()
        deviceRouting()
        notificationRouting()
        statisticRouting()
        stateTypeRouting()
        stateRouting()
        actionTypeRouting()
        actionRouting()
        conditionRouting()
        scriptRouting()
    }
}
