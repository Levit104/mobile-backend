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
import itmo.util.sendGet
import itmo.util.sendPost

// TODO: 10.04.2024
fun Route.actionRouting() {
    route("actions") {
        get {
            val userId = parseClaim<String>("userId", call)
            val deviceTypeId = call.request.queryParameters["deviceTypeId"]?.toIntOrNull()

            if (actionRedisRepository.isItemsExistsByDeviceTypeId(deviceTypeId.toString())) {
                log("GET /actions", userId, "Получение действий по deviceTypeId=$deviceTypeId из кэша", "success")
                call.respond(actionRedisRepository.getItemsByDeviceTypeId(deviceTypeId.toString()))
            } else {
                val response = sendGet("http://localhost:8080/actions", "deviceTypeId", deviceTypeId.toString())

                if (response.status == HttpStatusCode.OK) {
                    log("GET /actions", userId, "Получение действий по deviceTypeId=$deviceTypeId из БД", "success")
                    call.respond(response.body<List<ActionDTO>>())
                } else {
                    log("GET /actions", userId, response.bodyAsText(), "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            }
        }
        // FIXME не используется
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
            val response = sendPost("http://localhost:8080/actions", actionDAO)

            val logStatus = if (response.status == HttpStatusCode.OK) "success" else "fail"
            log("POST /actions", userId, response.bodyAsText(), logStatus)
            call.respond(response.status, response.bodyAsText())
        }
    }
}