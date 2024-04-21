package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.DeviceDAO
import itmo.cache.model.DeviceRedisRepository
import itmo.plugins.client


val deviceRedisRepository = DeviceRedisRepository()

// TODO: 10.04.2024  
fun Route.deviceRouting() {
    route("devices") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val userId = principal!!.payload.getClaim("userId").asInt()

            if (userRedisRepository.isItemExists(username)) {
                val userId = userRedisRepository.getUserByLogin(username)["id"].orEmpty()
                if (deviceRedisRepository.isItemsExistsByUser(userId)) {
                    call.respond(deviceRedisRepository.getItemsByUser(userId))
                }
            }

            val response: HttpResponse = client.get("http://localhost:8080/devices") {
                url {
                    parameters.append("userId", userId)
                }
            }
            if (response.status == HttpStatusCode.OK) {
                call.respond(response.body<List<DeviceDAO>>())
            } else {
                call.respond(HttpStatusCode.NoContent, "Ошибочка, какая хз")
            }
        }

        get("{id}") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val userId = principal!!.payload.getClaim("userId").asInt()
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null && deviceRedisRepository.isItemExistsByUser(id.toString(), username)) {
                call.respond(deviceRedisRepository.getItem(id.toString()))
            }

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/devices/$id") {
                    url {
                        parameters.append("userId", userId)
                    }
                }

                if (response.status == HttpStatusCode.OK) {
                    call.respond(response.body<List<DeviceDAO>>())
                }
            }

            call.respond(HttpStatusCode.Forbidden, "Устройство не существует или у вас нет доступа!")
        }

        post {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val device = call.receive<DeviceDAO>()

            val response: HttpResponse = client.post("http://localhost:8080/devices") {
                setBody(device)
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, "Устройство успешно добавлено!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Произошла ошибка при добавлении устройства")
            }
        }
    }
}