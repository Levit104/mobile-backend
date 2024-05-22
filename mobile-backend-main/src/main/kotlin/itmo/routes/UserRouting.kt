package itmo.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.UserRedisRepository
import itmo.util.log
import itmo.util.parseClaim


val userRedisRepository = UserRedisRepository()
fun Route.userRouting() {
    route("user") {
        get {
            
            val username = parseClaim<String>("username", call)
            val userId = parseClaim<String>("userId", call)

            log("user get cash", userId, "Пользователь получен из кэша", "success")
            call.respond(userRedisRepository.getUserByLogin(username))
        }
    }
}