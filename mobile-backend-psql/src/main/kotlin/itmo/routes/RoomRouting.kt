package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.RoomDAO
import itmo.models.Room
import itmo.util.log

fun Route.roomRouting() {
    val dao = RoomDAO()

    route("rooms") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                log("rooms get", "-1", "find all", "success")
                call.respond(dao.findAll())
            }

            val userId = call.request.queryParameters["userId"]?.toIntOrNull()

            if (userId == null || userId <= 0) {
                log("rooms get", "-1", "Не указан пользователь", "fail")
                call.respond(HttpStatusCode.BadRequest, "Не указан пользователь")
            } else {
                log("rooms get", "$userId", "find by userId $userId", "success")
                call.respond(dao.findAllByUser(userId))
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Room? = dao.findById(id)
                if (entity == null) {
                    log("rooms get id", "-1", "Комната с id=$id не найдена", "fail")
                    call.respond(HttpStatusCode.NotFound, "Комната с id=$id не найдена")
                } else {
                    log("rooms get id", "-1", "find by id $id", "success")
                    call.respond(entity)
                }
            }

            log("rooms get id", "-1", "no id", "fail")
        }
        post {
            try {
                val entity = call.receive<Room>()
                val notValid = entity.name.isBlank() || entity.userId <= 0
                if (notValid) {
                    log("rooms post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    // TODO проверка UNIQUE у пользователя
                    log("rooms post", "-1", "insert room name ${entity.name}", "success")
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                log("rooms post", "-1", "BadRequestException", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
        delete {
            val id = call.request.queryParameters["roomId"]?.toIntOrNull()

            if (id == null || id <= 0) {
                log("room delete", "-1", "Не указан id", "fail")
                call.respond(HttpStatusCode.BadRequest, "Нет указан id")
            } else {
                dao.deleteById(id)
                log("room delete", "-1", "Комната $id удалена", "success")
                call.respond(HttpStatusCode.OK, "Комната $id удалена")
            }
        }
    }
}