package itmo.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
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
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.util.*



val userRedisRepository = UserRedisRepository()
val client = HttpClient(CIO)

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
                val response: HttpResponse = client.get("http://localhost:8080/users/${user.username}")
                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.Conflict, "Пользователь с таким логином существует!")
                } else {
                    val response: HttpResponse = client.post("http://localhost:8080/users/") {
                        setBody(user)
                    }
                    if (response.status != HttpStatusCode.OK) {
                        call.respond(HttpStatusCode.Conflict, "Ошибка при создании пользователя!")
                    }
                    user.id = response.body<UserDAO>().id
                    userRedisRepository.addItem(user.id.toString(), user, 300000000)
                    call.respond(HttpStatusCode.Created, "Пользователь с логином ${user.username} успешно создан!")
                }
            }
        }
        post("/signIn") {
            val user = call.receive<UserDAO>()
            var isAuthorized = false
            if (userRedisRepository.isItemExists(user.username)) {
                isAuthorized = userRedisRepository.getUserByLogin(user.username)["password"].equals(user.password)
            } else {
                val response: HttpResponse = client.get("http://localhost:8080/users/${user.username}")
                if (response.status == HttpStatusCode.OK) {
                    isAuthorized = response.body<UserDAO>().password == user.password
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Пользователь не существует")
                }
            }
            if (isAuthorized) {
                val token = JWT.create()
                    .withAudience(Config.AUDIENCE.toString())
                    .withIssuer(Config.ISSUER.toString())
                    .withClaim("username", user.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 30000000))
                    .sign(Algorithm.HMAC256(Config.SECRET.toString()))
                call.respond(token)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Неправильный логин или пароль")
            }
        }
    }
}
