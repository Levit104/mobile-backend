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
import itmo.cache.model.DeviceDAO
import itmo.plugins.client
import itmo.util.log
import itmo.util.parseClaim

// TODO: 10.04.2024
fun Route.actionRouting() {
    route("actions") {

        get {
            
            val userId = parseClaim<String>("userId", call)

            val deviceTypeId = call.request.queryParameters["deviceId"]?.toIntOrNull()

            val response: HttpResponse = client.get("http://localhost:8080/actions") {
                url {
                    parameters.append("deviceTypeId", deviceTypeId.toString())
                }
            }

            if (response.status == HttpStatusCode.OK) {
                log("actions get", userId, "DeviceTypeId: $deviceTypeId", "success")
                call.respond(response.body<List<DeviceDAO>>())
            } else {
                log("actions get", userId, "Ошибочка, какая хз", "fail")
                call.respond(HttpStatusCode.NoContent, "Ошибочка, какая хз")
            }
        }

        get("{id}") {
            
            val userId = parseClaim<String>("userId", call)

            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/actions/$id")

                if (response.status == HttpStatusCode.OK) {
                    log("actions get id", userId, "Получено действие $id", "success")
                    call.respond(HttpStatusCode.OK, response.body<ActionDAO>())
                } else {
                    log("actions get id", userId, response.bodyAsText(), "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            }

            log("actions get id", userId, "Нет id", "fail")
            call.respond(HttpStatusCode.BadRequest, "Нет id")
        }


        post {
            
            val userId = parseClaim<String>("userId", call)

            val actionDAO = call.receive<ActionDAO>()

            val response: HttpResponse = client.post("http://localhost:8080/actions") {
                setBody(actionDAO)
            }

            if (response.status == HttpStatusCode.OK) {
                log("actions post", userId, "Действие успешно добавлено!", "success")
                call.respond(HttpStatusCode.OK, "Действие успешно добавлено!")
            } else {
                log("actions post", userId, response.bodyAsText(), "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }


    }
}