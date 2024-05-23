package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.StateDAO
import itmo.models.State
import itmo.util.log

fun Route.stateRouting() {
    val dao = StateDAO()

    route("states") {
        // FIXME не используется
        get {
            if (call.request.queryParameters.isEmpty()) {
                log("states get", "-1", "find all", "success")
                call.respond(dao.findAll())
            } else {
                val deviceId = call.request.queryParameters["deviceId"]?.toIntOrNull()

                if (deviceId == null || deviceId <= 0) {
                    log("states get", "-1", "Не указано устройство", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Не указано устройство")
                } else {
                    log("states get", "-1", "get by deviceId $deviceId", "success")
                    call.respond(dao.findAllByDevice(deviceId))
                }
            }
        }
        // FIXME не используется
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: State? = dao.findById(id)
                if (entity == null) {
                    log("states get id", "-1", "Состояние с id=$id не найдено", "fail")
                    call.respond(HttpStatusCode.NotFound, "Состояние с id=$id не найдено")
                } else {
                    log("states get id", "-1", "get states id ${entity.id}", "success")
                    call.respond(entity)
                }
            }
            log("states get id", "-1", "no id", "fail")
        }
        // FIXME не используется
        post {
            try {
                val entity = call.receive<State>()
                val notValid = entity.deviceId <= 0 || entity.actionId <= 0 || entity.value.isBlank()
                if (notValid) {
                    log("states post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    log("states post", "-1", "insert state", "success")
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                log("states post", "-1", "BadRequestException", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}