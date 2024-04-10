package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.ActionDAO
import itmo.models.Action

fun Route.actionRouting() {
    val dao = ActionDAO()

    route("actions") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                call.respond(dao.findAll())
            }

            val deviceTypeId = call.request.queryParameters["deviceTypeId"]?.toIntOrNull()

            if (deviceTypeId == null || deviceTypeId <= 0) {
                call.respond(HttpStatusCode.BadRequest, "Не указан тип устройства")
            } else {
                call.respond(dao.findAllByDeviceType(deviceTypeId))
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Action? = dao.findById(id)
                if (entity == null) {
                    call.respond(HttpStatusCode.NotFound, "Действие с id=$id не найдено")
                } else {
                    call.respond(entity)
                }
            }
        }
        post {
            try {
                val entity = call.receive<Action>()
                val notValid = entity.actionTypeId <= 0 && entity.deviceTypeId <= 0
                if (notValid) {
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}