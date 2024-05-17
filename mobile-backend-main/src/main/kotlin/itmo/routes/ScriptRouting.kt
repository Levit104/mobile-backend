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
import itmo.cache.model.ScriptDao
import itmo.plugins.client

// TODO: 10.04.2024
fun Route.scriptRouting() {
    route("scripts") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val response: HttpResponse = client.get("http://localhost:8080/scripts") {
                url {
                    parameters.append("userId", userId.toString())
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
            }

            call.respond(HttpStatusCode.BadRequest, "Нет id")
        }

        post {
            val script = call.receive<ScriptDao>()

            val response: HttpResponse = client.post("http://localhost:8080/scripts") {
                setBody(script)
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, "Сценарий успешно добавлен!")
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}