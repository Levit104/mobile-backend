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

// TODO: 10.04.2024
fun Route.conditionRouting() {
    route("conditions") {

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

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/conditions/$id")

                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.OK, response.body<ConditionDAO>())
                } else {
                    call.respond(response.status, response.bodyAsText())
                }
            }

            call.respond(HttpStatusCode.BadRequest, "Нет id")
        }

        post {
            val condition = call.receive<ConditionDAO>()

            val response: HttpResponse = client.post("http://localhost:8080/conditions") {
                setBody(condition)
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}