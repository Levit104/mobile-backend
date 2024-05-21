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
import itmo.util.log


val deviceRedisRepository = DeviceRedisRepository()

// TODO: 10.04.2024  
fun Route.deviceRouting() {
    route("devices") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val userId = principal!!.payload.getClaim("userId").asString()

            if (userRedisRepository.isItemExists(username)) {
                if (deviceRedisRepository.isItemsExistsByUser(userId)) {
                    log("devices get cash", userId, "Устройства получены из кэша", "success")
                    call.respond(deviceRedisRepository.getItemsByUser(userId))
                }
            }

            val response: HttpResponse = client.get("http://localhost:8080/devices") {
                url {
                    parameters.append("userId", userId.toString())
                }
            }
            if (response.status == HttpStatusCode.OK) {
                log("devices get", userId, "Устройства получены успешно", "success")
                call.respond(response.body<List<DeviceDAO>>())
            } else {
                log("devices get", userId, "Ошибочка, какая хз", "fail")
                call.respond(HttpStatusCode.NoContent, "Ошибочка, какая хз")
            }
        }

        get("{id}") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val userId = principal!!.payload.getClaim("userId").asString()
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null && deviceRedisRepository.isItemExistsByUser(id.toString(), username)) {
                log("devices get id cash", userId, "Устройство получено из кэша id $id", "success")
                call.respond(deviceRedisRepository.getItem(id.toString()))
            }

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/devices/$id") {
                    url {
                        parameters.append("userId", userId.toString())
                    }
                }

                if (response.status == HttpStatusCode.OK) {
                    log("devices get id", userId, "Устройство получено успешно id $id", "success")
                    call.respond(response.body<DeviceDAO>())
                }
            }

            log("devices get id", userId, "Устройство не существует или у вас нет доступа!", "fail")
            call.respond(HttpStatusCode.Forbidden, "Устройство не существует или у вас нет доступа!")
        }

        post {
            val device = call.receive<DeviceDAO>()
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asString()

            val response: HttpResponse = client.post("http://localhost:8080/devices") {
                setBody(device)
            }

            if (response.status == HttpStatusCode.OK) {
                log("devices post", userId, "Устройство успешно добавлено! ${device.name}", "success")
                call.respond(HttpStatusCode.OK, "Устройство успешно добавлено!")
            } else {
                log("devices post", userId, "Произошла ошибка при добавлении устройства ${device.name}", "fail")
                call.respond(HttpStatusCode.BadRequest, "Произошла ошибка при добавлении устройства")
            }
        }
    }
}