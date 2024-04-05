package itmo.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.UserRedisRepository


val userRedisRepository = UserRedisRepository()
fun Route.userRouting() {
    route("user") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            call.respond(userRedisRepository.getUserByLogin(username))
        }
    }
}