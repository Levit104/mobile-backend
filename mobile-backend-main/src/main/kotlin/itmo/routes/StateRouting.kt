package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.StateDAO
import itmo.plugins.client
import itmo.util.log
import itmo.util.parseClaim
import itmo.util.sendPost

// TODO: 10.04.2024
fun Route.stateRouting() {
    route("states") {
        get {
            val response: HttpResponse = client.get("http://localhost:8080/states")

            val userId = parseClaim<String>("userId", call)

            if (response.status == HttpStatusCode.OK) {
                log("states get", userId, "Успешно получены все состояния", "success")
                call.respond(HttpStatusCode.OK, response.body<StateDAO>())
            } else {
                log("states get", userId, "Ошибка при получение всех состояний", "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            val userId = parseClaim<String>("userId", call)

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/states/$id")

                if (response.status == HttpStatusCode.OK) {
                    log("states get id", userId, "Успешно получено состояние $id", "success")
                    call.respond(HttpStatusCode.OK, response.body<StateDAO>())
                } else {
                    log("states get id", userId, "Ошибка при получение состояния $id", "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            } else {
                log("states get id", userId, "Нет id", "fail")
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }
        post {
            val stateDAO = call.receive<StateDAO>()

            val userId = parseClaim<String>("userId", call)

            val response: HttpResponse = sendPost("http://localhost:8080/states", stateDAO)

            if (response.status == HttpStatusCode.OK) {
                log("states post", userId, "Успешно добавлено состояние ${response.bodyAsText()}", "success")
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                log("states post", userId, "Ошибка при добавление состояния", "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }

        put {
            val stateDAO = call.receive<StateDAO>()

            val userId = parseClaim<String>("userId", call)

            val response: HttpResponse = client.put("http://localhost:8080/states") {
                contentType(ContentType.Application.Json)
                setBody(stateDAO)
            }

            if (response.status == HttpStatusCode.OK) {
                log("states put", userId, "Успешно обновлено состояние ${response.bodyAsText()}", "success")
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                log("states put", userId, "Ошибка при обновление состояния", "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}