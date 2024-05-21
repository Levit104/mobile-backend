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
import itmo.util.log

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
                log("room get", "$userId", "Получены комнаты пользователя", "success")
                call.respond(HttpStatusCode.OK, response.body<List<RoomDAO>>())
            } else {
                log("room get", "$userId", "Не удалось получить комнаты пользователя", "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/rooms/$id")

                if (response.status == HttpStatusCode.OK) {
                    log("room get id", "$userId", "Получена комната пользователя по id $id", "success")
                    call.respond(HttpStatusCode.OK, response.body<RoomDAO>())
                } else {
                    log("room get id", "$userId", "${response.bodyAsText()} id $id", "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            }

            log("room get id", "$userId", "Нет id", "fail")
            call.respond(HttpStatusCode.BadRequest, "Нет id")
        }

        post {
            val room = call.receive<RoomDAO>()
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val response: HttpResponse = client.post("http://localhost:8080/rooms") {
                setBody(room)
            }

            if (response.status == HttpStatusCode.OK) {
                log("room post", "$userId", "Комната успешно добавлена! ${room.name}", "success")
                call.respond(HttpStatusCode.OK, "Комната успешно добавлена!")
            } else {
                log("room post", "$userId", "Ошибка при добавлении комнаты ${room.name}", "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}