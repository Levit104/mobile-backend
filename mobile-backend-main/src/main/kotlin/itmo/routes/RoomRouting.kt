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
import itmo.util.log
import itmo.util.parseClaim
import itmo.util.sendPost

// TODO: 10.04.2024  
fun Route.roomRouting() {
    route("rooms") {
        get {
            val userId = parseClaim<String>("userId", call)

            val response: HttpResponse = client.get("http://localhost:8080/rooms") {
                url {
                    parameters.append("userId", userId)
                }
            }

            if (response.status == HttpStatusCode.OK) {
                log("room get", userId, "Получены комнаты пользователя", "success")
                call.respond(HttpStatusCode.OK, response.body<List<RoomDAO>>())
            } else {
                log("room get", userId, "Не удалось получить комнаты пользователя", "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            val userId = parseClaim<String>("userId", call)

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/rooms/$id")

                if (response.status == HttpStatusCode.OK) {
                    log("room get id", userId, "Получена комната пользователя по id $id", "success")
                    call.respond(HttpStatusCode.OK, response.body<RoomDAO>())
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

            val response: HttpResponse = sendPost(
                "http://localhost:8080/rooms",
                RoomDAO(null, room.name, userId.toLong())
            )

            if (response.status == HttpStatusCode.OK) {
                log("room post", userId, "Комната успешно добавлена! ${room.name}", "success")
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                log("room post", userId, "Ошибка при добавлении комнаты ${room.name}", "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }

        delete {
            val userId = parseClaim<String>("userId", call)

            val roomId = call.request.queryParameters["roomId"]?.toIntOrNull()

            if (roomId !== null) {
                val response: HttpResponse = client.delete("http://localhost:8080/rooms") {
                    url {
                        parameters.append("roomId", roomId.toString())
                    }
                }

                if (response.status == HttpStatusCode.OK) {
                    log("room delete", userId, "Успешно удалилась", "success")
                    call.respond(HttpStatusCode.OK, "Успешно удалилась")
                } else {
                    log("room delete", userId, "Не удалось удалить $roomId", "fail")
                    call.respond(HttpStatusCode.NoContent, "Ошибочка, какая хз")
                }
            } else {
                log("room delete", userId, "Нет id", "fail")
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }
    }
}