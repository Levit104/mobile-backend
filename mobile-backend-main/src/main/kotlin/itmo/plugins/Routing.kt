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
import itmo.cache.model.*
import itmo.jedisPool
import itmo.routes.*
import itmo.util.log
import itmo.util.sendPost
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.util.*


val userRedisRepository = UserRedisRepository()
@OptIn(ExperimentalSerializationApi::class)
val client = HttpClient(CIO) {
    engine {
        endpoint.maxConnectionsPerRoute = 100
        maxConnectionsCount = 1000
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 5000
    }
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            explicitNulls = false
            prettyPrint = true
            isLenient = true
        })
    }
}

val actionRedisRepository = ActionRedisRepository()
val deviceTypeRedisRepository = DeviceTypeRedisRepository()

fun Application.configureRouting() {
    val con = jedisPool.resource
    con.flushAll()
    con.close()
    launch {
        val response = client.get("http://localhost:8080/device-types")

        if (response.status == HttpStatusCode.OK) {
            log("launch 1", "-1", "Получение всех типов девайсов при запуске", "success")
            val types = response.body<List<DeviceTypeDAO>>()
            types.forEach { type -> deviceTypeRedisRepository.addItem(type.id.toString(), type.name) }
        } else {
            log("launch 1", "-1", response.bodyAsText(), "fail")
        }
    }
    launch {
        val response = client.get("http://localhost:8080/actions")

        if (response.status == HttpStatusCode.OK) {
            log("launch 2", "-1", "Получение всех действий при запуске", "success")
            val actions = response.body<List<ActionDTO>>()
            actions.forEach { action -> actionRedisRepository.addItem(action.id.toString(), action) }
        } else {
            log("launch 2", "-1", response.bodyAsText(), "fail")
        }
    }
    routing {
        authenticate {
            deviceRouting()
            deviceTypeRouting()
            stateRouting()
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
                log("POST /signUp", "-1", "Пользователь с login=${user.login} уже существует!", "fail")
                call.respond(HttpStatusCode.Conflict, "Пользователь с login=${user.login} уже существует!")
            } else {
                val response = sendPost("http://localhost:8080/users", user)
                if (response.status == HttpStatusCode.OK) {
                    user.id = response.body<String>().toLong()
                    userRedisRepository.addItem(user.id.toString(), user, 30000000)

                    log("POST /signUp", "${user.id}", "Пользователь с логином ${user.login} успешно создан!", "success")
                    call.respond(HttpStatusCode.Created, "Пользователь с логином ${user.login} успешно создан!")
                } else {
                    log("POST /signUp", "-1", response.bodyAsText(), "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            }
        }
        post("/signIn") {
            val user = call.receive<UserDAO>()
            var isAuthorized = false
            var userId = ""
            if (userRedisRepository.isItemExists(user.login)) {
                val obj = userRedisRepository.getUserByLogin(user.login)
                isAuthorized = obj.password.equals(user.password)
                userId = obj.id.toString()

                log("POST /signIn", userId, "Пользователь #$userId получен из кэша", "success")
            } else {
                val response = client.get("http://localhost:8080/users/${user.login}")
                if (response.status == HttpStatusCode.OK) {
                    val userDAO = response.body<UserDAO>()
                    isAuthorized = userDAO.password == user.password
                    userRedisRepository.addItem(userDAO.id.toString(), userDAO, 30000000)
                    userId = userDAO.id.toString()

                    log("POST /signIn", userId, "Пользователь #$userId получен из из БД", "success")
                } else {
                    log("POST /signIn", "-1", response.bodyAsText(), "fail")
                    call.respond(response.status, response.bodyAsText())
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

                log("POST /signIn", userId, "Токен создан $token", "success")
                call.respond(token)
            } else {
                log("POST /signIn", userId, "Неправильный логин или пароль", "fail")
                call.respond(HttpStatusCode.Unauthorized, "Неправильный логин или пароль")
            }
        }
    }
}
