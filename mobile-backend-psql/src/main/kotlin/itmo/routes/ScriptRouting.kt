package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.ScriptDAO
import itmo.models.Script
import itmo.util.log

fun Route.scriptRouting() {
    val dao = ScriptDAO()

    route("scripts") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                log("scripts get", "-1", "find all", "success")
                call.respond(dao.findAll())
            }

            val userId = call.request.queryParameters["userId"]?.toIntOrNull()

            if (userId == null || userId <= 0) {
                log("scripts get", "-1", "Не указан пользователь", "fail")
                call.respond(HttpStatusCode.BadRequest, "Не указан пользователь")
            } else {
                log("scripts get", "$userId", "find by $userId", "success")
                call.respond(dao.findAllByUser(userId))
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Script? = dao.findById(id)
                if (entity == null) {
                    log("scripts get id", "-1", "Сценарий с id=$id не найден", "fail")
                    call.respond(HttpStatusCode.NotFound, "Сценарий с id=$id не найден")
                } else {
                    log("scripts get id", "-1", "get by id $id", "success")
                    call.respond(entity)
                }
            }
            log("scripts get id", "-1", "no id", "fail")
        }
        post {
            try {
                val entity = call.receive<Script>()
                val notValid = entity.deviceId <= 0 || entity.conditionId <= 0 || entity.actionId <= 0
                        || entity.conditionValue.isBlank() || entity.actionValue.isBlank()
                if (notValid) {
                    log("scripts post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    log("scripts post", "-1", "insert scripts", "success")
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                log("scripts post", "-1", "BadRequestException", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}