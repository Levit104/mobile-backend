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
                log("GET /rooms", "-1", "Получение всех комнат", "success")
                call.respond(dao.findAll())
            } else {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()

                try {
                    if (userId == null || userId <= 0) {
                        throw BadRequestException("Указан некорректный userId=$userId")
                    }

                    log(
                        "GET /rooms?userId=$userId",
                        "$userId",
                        "Получение всех комнат у пользователя #$userId",
                        "success"
                    )

                    call.respond(dao.findAllByUser(userId))

                } catch (e: BadRequestException) {
                    log(
                        "GET /rooms?userId=$userId",
                        "-1",
                        "Ошибка при получении комнат: ${e.message}",
                        "fail"
                    )

                    call.respond(HttpStatusCode.BadRequest, "Ошибка при получении комнат: ${e.message}")
                }
            }
        }
        // FIXME не используется
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
                    throw BadRequestException("Некорректное значение одного или нескольких полей - name, userId")
                }

                log(
                    "POST /rooms",
                    "${entity.userId}",
                    "Добавлена комната с именем ${entity.name} пользователю #${entity.userId}",
                    "success"
                )

                call.respond(dao.insert(entity))

            } catch (e: BadRequestException) {
                log(
                    "POST /actions",
                    "-1",
                    "Ошибка при выполнении действия: ${e.message}",
                    "fail"
                )

                call.respond(HttpStatusCode.BadRequest, "Ошибка при выполнении действия: ${e.message}")
            }
        }
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            try {
                if (id == null || id <= 0) {
                    throw BadRequestException("Указан некорректный id=$id")
                }

                if (dao.findById(id) == null) {
                    throw BadRequestException("Комната #$id не существует")
                }

                dao.deleteById(id)
                log("DELETE /rooms/$id", "-1", "Комната #$id удалёна", "success")
                call.respond(HttpStatusCode.OK, "Комната #$id удалена")

            } catch (e: BadRequestException) {
                log(
                    "DELETE /rooms/$id",
                    "-1",
                    "Ошибка при удалении комнаты: ${e.message}",
                    "fail"
                )

                call.respond(HttpStatusCode.BadRequest, "Ошибка при удалении комнаты: ${e.message}")
            }
        }
    }
}