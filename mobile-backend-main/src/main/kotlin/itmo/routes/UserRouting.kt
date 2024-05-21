package itmo.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.UserRedisRepository
import itmo.util.log


val userRedisRepository = UserRedisRepository()
fun Route.userRouting() {
    route("user") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val userId = principal!!.payload.getClaim("userId").asString()

            log("user get cash", userId, "Пользователь получен из кэша", "success")
            call.respond(userRedisRepository.getUserByLogin(username))
        }
    }
}