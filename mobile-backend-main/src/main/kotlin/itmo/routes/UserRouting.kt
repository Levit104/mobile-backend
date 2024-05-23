package itmo.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.util.log
import itmo.util.parseClaim

fun Route.userRouting() {
    route("user") {
        // FIXME не используется
        get {

            val username = parseClaim<String>("username", call)
            val userId = parseClaim<String>("userId", call)

            log("user get", userId, "Пользователь получен", "success")
            call.respond(username)
        }
    }
}