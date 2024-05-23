package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.RoomDAO
import itmo.plugins.client
import itmo.util.*

// TODO: 10.04.2024  
fun Route.roomRouting() {
    route("rooms") {
        get {
            val userId = parseClaim<String>("userId", call)
            val response = sendGet("http://localhost:8080/rooms", "userId", userId)

            if (response.status == HttpStatusCode.OK) {
                log("GET /rooms?userId=$userId", userId, "Получение всех комнат у пользователя #$userId", "success")
                call.respond(response.body<List<RoomDAO>>())
            } else {
                log("GET /rooms?userId=$userId", userId, response.bodyAsText(), "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }
        // FIXME не используется
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            val userId = parseClaim<String>("userId", call)

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/rooms/$id")

                if (response.status == HttpStatusCode.OK) {
                    log("room get id", userId, "Получена комната пользователя по id $id", "success")
                    call.respond(response.body<RoomDAO>())
                } else {
                    log("room get id", userId, "${response.bodyAsText()} id $id", "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            } else {
                log("room get id", userId, "Нет id", "fail")
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }
        post {
            val room = call.receive<RoomDAO>()
            val userId = parseClaim<String>("userId", call)
            val response = sendPost(
                "http://localhost:8080/rooms",
                RoomDAO(null, room.name, userId.toLong())
            )

            if (response.status == HttpStatusCode.OK) {
                log(
                    "POST /rooms",
                    userId,
                    "Добавлена комната #${response.bodyAsText()} пользователю #${userId}",
                    "success"
                )
            } else {
                log("POST /rooms", userId, response.bodyAsText(), "fail")
            }

            call.respond(response.status, response.bodyAsText())
        }
        delete {
            val userId = parseClaim<String>("userId", call)
            val roomId = call.request.queryParameters["roomId"]?.toIntOrNull()
            val response = sendDelete("http://localhost:8080/rooms", "roomId", roomId.toString())

            val logStatus = if (response.status == HttpStatusCode.OK) "success" else "fail"
            log("DELETE /rooms?roomId=$roomId", userId, response.bodyAsText(), logStatus)
            call.respond(response.status, response.bodyAsText())
        }
    }
}