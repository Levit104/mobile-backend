package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.RoomDAO
import itmo.models.Room

fun Route.roomRouting() {
    val dao = RoomDAO()

    route("rooms") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                call.respond(dao.findAll())
            }

            val userId = call.request.queryParameters["userId"]?.toIntOrNull()

            if (userId == null || userId <= 0) {
                call.respond(HttpStatusCode.BadRequest, "Не указан пользователь")
            } else {
                call.respond(dao.findAllByUser(userId))
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Room? = dao.findById(id)
                if (entity == null) {
                    call.respond(HttpStatusCode.NotFound, "Комната с id=$id не найдена")
                } else {
                    call.respond(entity)
                }
            }
        }
        post {
            try {
                val entity = call.receive<Room>()
                val notValid = entity.name.isBlank() || entity.userId <= 0
                if (notValid) {
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    // TODO проверка UNIQUE у пользователя
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}