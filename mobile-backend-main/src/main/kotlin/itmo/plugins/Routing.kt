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
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import itmo.routes.*
import itmo.routes.roomRouting
import kotlinx.serialization.json.Json
import java.util.*



val userRedisRepository = UserRedisRepository()
val client = HttpClient(CIO) {
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}

fun Application.configureRouting() {
        routing {
        authenticate {
            deviceRouting()
            deviceTypeRouting()
            stateTypeRouting()
            stateRouting()
            actionTypeRouting()
            conditionRouting()
            scriptRouting()
            actionRouting()
            roomRouting()
            userRouting()
        }
        post("/signUp") {
            val user = call.receive<UserDAO>()
            if (userRedisRepository.isItemExists(user.login)) {
                call.respond(HttpStatusCode.Conflict, "Пользователь с таким логином существует!")
            } else {
                val response: HttpResponse = client.get("http://localhost:8080/users/${user.login}")
                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.Conflict, "Пользователь с таким логином существует!")
                } else {
                    val response: HttpResponse = client.post("http://localhost:8080/users") {
                        contentType(ContentType.Application.Json)
                        setBody(user)
                    }
                    if (response.status != HttpStatusCode.OK) {
                        call.respond(HttpStatusCode.Conflict, "Ошибка при создании пользователя!")
                    }
                    user.id = response.body<String>().toLong()
                    userRedisRepository.addItem(user.id.toString(), user, 300000000)
                    call.respond(HttpStatusCode.Created, "Пользователь с логином ${user.login} успешно создан!")
                }
            }
        }
        post("/signIn") {
            val user = call.receive<UserDAO>()
            var isAuthorized = false
            if (userRedisRepository.isItemExists(user.login)) {
                isAuthorized = userRedisRepository.getUserByLogin(user.login)["password"].equals(user.password)
            } else {
                val response: HttpResponse = client.get("http://localhost:8080/users/${user.login}")
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
                    .withClaim("username", user.login)
                    .withClaim("userId", user.id)
                    .withExpiresAt(Date(System.currentTimeMillis() + 30000000))
                    .sign(Algorithm.HMAC256(Config.SECRET.toString()))
                call.respond(token)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Неправильный логин или пароль")
            }
        }
    }
}
