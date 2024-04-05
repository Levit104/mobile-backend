package itmo.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.Config
import itmo.cache.model.UserDAO
import itmo.cache.model.UserRedisRepository
import itmo.routes.deviceRouting
import itmo.routes.userRouting
import java.util.*



val userRedisRepository = UserRedisRepository()

fun Application.configureRouting() {
        routing {
        authenticate {
            deviceRouting()
            userRouting()
        }
        post("/signUp") {
            val user = call.receive<UserDAO>()
            if (userRedisRepository.isItemExists(user.username)) {
                call.respond(HttpStatusCode.Conflict, "Пользователь с таким логином существует!")
            } else {
                // Нужно передавать id, сгенерированный PSQL
                user.id = 1
                userRedisRepository.addItem(user.id.toString(), user, 300000000)
                call.respond(HttpStatusCode.Created, "Пользователь с логином ${user.username} успешно создан!")
            }
        }
        post("/signIn") {
            val user = call.receive<UserDAO>()
            if (userRedisRepository.isItemExists(user.username) && userRedisRepository.getUserByLogin(user.username)["password"] == user.password) {
                val token = JWT.create()
                    .withAudience(Config.AUDIENCE.toString())
                    .withIssuer(Config.ISSUER.toString())
                    .withClaim("username", user.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 30000000))
                    .sign(Algorithm.HMAC256(Config.SECRET.toString()))
                call.respond(token)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Пользователь не существует или неправильные данные")
            }
        }
    }
}
