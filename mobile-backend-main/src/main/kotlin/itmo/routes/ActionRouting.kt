package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.ActionDAO
import itmo.cache.model.ActionDTO
import itmo.plugins.client
import itmo.util.log
import itmo.util.parseClaim
import itmo.util.sendPost

// TODO: 10.04.2024
fun Route.actionRouting() {
    route("actions") {
        get {
            val userId = parseClaim<String>("userId", call)

            val deviceTypeId = call.request.queryParameters["deviceTypeId"]?.toIntOrNull()

            if (actionRedisRepository.isItemsExistsByDeviceTypeId(deviceTypeId.toString())) {
                log("actions get", userId, "DeviceTypeId: $deviceTypeId", "success")
                call.respond(actionRedisRepository.getItemsByDeviceTypeId(deviceTypeId.toString()))
            } else {
                val response: HttpResponse = client.get("http://localhost:8080/actions") {
                    url {
                        parameters.append("deviceTypeId", deviceTypeId.toString())
                    }
                }

                if (response.status == HttpStatusCode.OK) {
                    log("actions get", userId, "DeviceTypeId: $deviceTypeId", "success")
                    call.respond(response.body<List<ActionDTO>>())
                } else {
                    log("actions get", userId, "Ошибочка, какая хз", "fail")
                    call.respond(HttpStatusCode.NoContent, "Ошибочка, какая хз")
                }
            }
        }
        get("{id}") {
            val userId = parseClaim<String>("userId", call)

            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                if (actionRedisRepository.isItemExists(id.toString())) {
                    log("actions get id", userId, "Получено действие $id", "success")
                    call.respond(HttpStatusCode.OK, actionRedisRepository.getItem(id.toString()))
                } else {
                    val response: HttpResponse = client.get("http://localhost:8080/actions/$id")

                    if (response.status == HttpStatusCode.OK) {
                        log("actions get id", userId, "Получено действие $id", "success")
                        call.respond(HttpStatusCode.OK, response.body<ActionDTO>())
                    } else {
                        log("actions get id", userId, response.bodyAsText(), "fail")
                        call.respond(response.status, response.bodyAsText())
                    }
                }
            } else {
                log("actions get id", userId, "Нет id", "fail")
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }
        post {
            val userId = parseClaim<String>("userId", call)

            val actionDAO = call.receive<ActionDAO>()

            val response: HttpResponse = sendPost("http://localhost:8080/actions", actionDAO)

            if (response.status == HttpStatusCode.OK) {
                log("actions post", userId, "Действие успешно добавлено!", "success")
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                log("actions post", userId, response.bodyAsText(), "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }


    }
}