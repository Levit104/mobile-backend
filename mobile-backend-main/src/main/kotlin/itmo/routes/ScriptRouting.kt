package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.ScriptDao
import itmo.plugins.client
import itmo.util.parseClaim
import itmo.util.sendPost

// TODO: 10.04.2024
fun Route.scriptRouting() {
    route("scripts") {
        get {
            val userId = parseClaim<String>("userId", call)

            val response: HttpResponse = client.get("http://localhost:8080/scripts") {
                url {
                    parameters.append("userId", userId)
                }
            }
            if (response.status == HttpStatusCode.OK) {
                call.respond(response.body<List<ScriptDao>>())
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/scripts/$id")

                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.OK, response.body<ScriptDao>())
                } else {
                    call.respond(response.status, response.bodyAsText())
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }
        post {
            val script = call.receive<ScriptDao>()

            val response: HttpResponse = sendPost("http://localhost:8080/scripts", script)

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}