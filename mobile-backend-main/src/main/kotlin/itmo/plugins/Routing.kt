package itmo.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.Config
import itmo.cache.model.UserDAO
import itmo.cache.model.UserRedisRepository
import itmo.routes.*
import kotlinx.serialization.json.Json
import java.util.*


val userRedisRepository = UserRedisRepository()
val client = HttpClient(CIO) {
    engine {
        endpoint.maxConnectionsPerRoute = 100
        maxConnectionsCount = 1000
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 1000
    }
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
            conditionRouting()
        }
        post("/signUp") {
            val user = call.receive<UserDAO>()
            if (userRedisRepository.isItemExists(user.login)) {
                call.respond(HttpStatusCode.Conflict, "Пользователь с таким логином существует!")
            } else {
                val response: HttpResponse = client.post("http://localhost:8080/users") {
                    contentType(ContentType.Application.Json)
                    setBody(user)
                }
                if (response.status != HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.Conflict, "Ошибка при создании пользователя!")
                } else {
                    user.id = response.body<String>().toLong()
                    userRedisRepository.addItem(user.id.toString(), user, 30000000)
                    call.respond(HttpStatusCode.Created, "Пользователь с логином ${user.login} успешно создан!")
                }
            }
        }
        post("/signIn") {
            val user = call.receive<UserDAO>()
            var isAuthorized = false
            var userId = ""
            if (userRedisRepository.isItemExists(user.login)) {
                val obj = userRedisRepository.getUserByLogin(user.login)
                isAuthorized = obj["password"].equals(user.password)
                userId = obj["id"].toString()
            } else {
                val response: HttpResponse = client.get("http://localhost:8080/users/${user.login}")
                if (response.status == HttpStatusCode.OK) {
                    val userDAO = response.body<UserDAO>()
                    isAuthorized = userDAO.password == user.password
                    userRedisRepository.addItem(userDAO.id.toString(), userDAO, 30000000)
                    userId = userDAO.id.toString()
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Пользователь не существует")
                }
            }
            if (isAuthorized) {
                val token = JWT.create()
                    .withAudience(Config.AUDIENCE.toString())
                    .withIssuer(Config.ISSUER.toString())
                    .withClaim("username", user.login)
                    .withClaim("userId", userId)
                    .withExpiresAt(Date(System.currentTimeMillis() + 30000000))
                    .sign(Algorithm.HMAC256(Config.SECRET.toString()))
                call.respond(token)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Неправильный логин или пароль")
            }
        }
    }
}
