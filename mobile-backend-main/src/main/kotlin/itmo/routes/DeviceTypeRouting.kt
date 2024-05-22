package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.DeviceTypeDAO
import itmo.plugins.client
import itmo.plugins.deviceTypeRedisRepository
import itmo.util.log
import itmo.util.parseClaim
import itmo.util.sendPost

// TODO: 10.04.2024
fun Route.deviceTypeRouting() {
    route("device-types") {
        get {
            val userId = parseClaim<String>("userId", call)

            val types = deviceTypeRedisRepository.getItems()
            if (types.isNotEmpty()) {
                log("device-types get", userId, "Получены все типы устройств из кэша", "success")
                call.respond(HttpStatusCode.OK, types)
            } else {
                val response: HttpResponse = client.get("http://localhost:8080/device-types")

                if (response.status == HttpStatusCode.OK) {
                    log("device-types get", userId, "Получены все типы устройств", "success")
                    call.respond(HttpStatusCode.OK, response.body<List<DeviceTypeDAO>>())
                } else {
                    log("device-types get", userId, response.bodyAsText(), "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            }
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                if (deviceTypeRedisRepository.isItemExists(id.toString())) {
                    call.respond(HttpStatusCode.OK, deviceTypeRedisRepository.getItem(id.toString()))
                } else {
                    val response: HttpResponse = client.get("http://localhost:8080/device-types/$id")

                    if (response.status == HttpStatusCode.OK) {
                        call.respond(HttpStatusCode.OK, response.body<DeviceTypeDAO>())
                    } else {
                        call.respond(response.status, response.bodyAsText())
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }

        post {
            val deviceType = call.receive<DeviceTypeDAO>()

            val response: HttpResponse = sendPost("http://localhost:8080/device-types", deviceType)

            if (response.status == HttpStatusCode.OK) {
                deviceTypeRedisRepository.addItem(response.bodyAsText(), deviceType.name)
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}