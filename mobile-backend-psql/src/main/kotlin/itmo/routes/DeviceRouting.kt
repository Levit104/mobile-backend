package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.DeviceDAO
import itmo.models.Device
import itmo.util.log


fun Route.deviceRouting() {
    val dao = DeviceDAO()

    route("devices") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                log("devices get", "-1", "find all", "success")
                call.respond(dao.findAll())
            }

            val userId = call.request.queryParameters["userId"]?.toIntOrNull()

            if (userId == null || userId <= 0) {
                log("devices get", "-1", "Не указан пользователь", "fail")
                call.respond(HttpStatusCode.BadRequest, "Не указан пользователь")
            } else {
                log("devices get", "$userId", "devices by userId $userId", "success")
                call.respond(dao.findAllByUser(userId))
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Device? = dao.findById(id)
                if (entity == null) {
                    log("devices get id", "-1", "Устройство с id=$id не найден", "fail")
                    call.respond(HttpStatusCode.NotFound, "Устройство с id=$id не найден")
                } else {
                    log("devices get id", "-1", "get device by id $id", "success")
                    call.respond(entity)
                }
            }
            log("devices get id", "-1", "no id", "fail")
        }
        post {
            try {
                val entity = call.receive<Device>()
                val notValid = entity.name.isBlank() || entity.typeId <= 0 || entity.userId <= 0
                if (notValid) {
                    log("devices post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    // TODO проверка UNIQUE у пользователя
                    log("devices post", "${entity.userId}", "Insert device name ${entity.name}", "success")
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                log("devices post", "-1", "BadRequestException", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}