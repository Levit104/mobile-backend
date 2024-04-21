package itmo.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

// TODO: 10.04.2024  
fun Route.roomRouting() {
    route("room") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val response: HttpResponse = client.get("http://localhost:8080/rooms") {
                url {
                    parameters.append("userId", userId)
                }
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, response.body<List<RoomDAO>>())
            } else {
                call.respond(response.status, call.response.message())
            }
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/rooms/$id")

                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.OK, response.body<RoomDAO>())
                } else {
                    call.respond(response.status, call.response.message())
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
                call.respond(response.status, call.response.message())
            }
        }
    }
}