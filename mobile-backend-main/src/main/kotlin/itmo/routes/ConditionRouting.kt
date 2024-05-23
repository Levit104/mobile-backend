package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.ConditionDAO
import itmo.plugins.client
import itmo.util.log
import itmo.util.parseClaim
import itmo.util.sendPost

// TODO: 10.04.2024
fun Route.conditionRouting() {
    route("conditions") {
        // FIXME не используется
        get {
            val userId = parseClaim<String>("userId", call)

            val response: HttpResponse = client.get("http://localhost:8080/conditions")

            if (response.status == HttpStatusCode.OK) {
                log("conditions get", userId, "Получены все условия", "success")
                call.respond(HttpStatusCode.OK, response.body<ConditionDAO>())
            } else {
                log("conditions get", userId, response.bodyAsText(), "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }
        // FIXME не используется
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            val userId = parseClaim<String>("userId", call)

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/conditions/$id")

                if (response.status == HttpStatusCode.OK) {
                    log("conditions get id", userId, "Успешно получено условие $id", "success")
                    call.respond(HttpStatusCode.OK, response.body<ConditionDAO>())
                } else {
                    log("conditions get id", userId, "Не получено условие $id", "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            } else {
                log("conditions get id", userId, "Нет id", "fail")
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }
        // FIXME не используется
        post {
            val condition = call.receive<ConditionDAO>()

            val userId = parseClaim<String>("userId", call)

            val response: HttpResponse = sendPost("http://localhost:8080/conditions", condition)

            if (response.status == HttpStatusCode.OK) {
                log("conditions post", userId, "Добавлено условие ${condition.description}", "success")
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                log("conditions post", userId, response.bodyAsText(), "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}