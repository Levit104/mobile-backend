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
import itmo.cache.model.RoomDAO
import itmo.plugins.client

// TODO: 10.04.2024  
fun Route.roomRouting() {
    route("room") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val response: HttpResponse = client.get("http://localhost:8080/rooms") {
                url {
                    parameters.append("userId", userId.toString())
                }
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, response.body<List<RoomDAO>>())
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/rooms/$id")

                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.OK, response.body<RoomDAO>())
                } else {
                    call.respond(response.status, response.bodyAsText())
                }
            }

            call.respond(HttpStatusCode.BadRequest, "Нет id")
        }

        post {
            val room = call.receive<RoomDAO>()

            val response: HttpResponse = client.post("http://localhost:8080/rooms") {
                setBody(room)
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, "Комната успешно добавлена!")
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}