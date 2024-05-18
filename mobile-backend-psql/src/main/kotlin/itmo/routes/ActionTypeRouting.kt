package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.ActionTypeDAO
import itmo.models.ActionType
import itmo.util.log

fun Route.actionTypeRouting() {
    val dao = ActionTypeDAO()

    route("action-types") {
        get {
            log("action-types get", "-1", "get all", "success")
            call.respond(dao.findAll())
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: ActionType? = dao.findById(id)
                if (entity == null) {
                    log("action-types get id", "-1", "Тип действия с id=$id не найден", "fail")
                    call.respond(HttpStatusCode.NotFound, "Тип действия с id=$id не найден")
                } else {
                    log("action-types get id", "-1", "get action-types ${entity.id}", "success")
                    call.respond(entity)
                }
            }
            log("action-types get id", "-1", "no id", "fail")
        }
        post {
            try {
                val entity = call.receive<ActionType>()
                val notValid = entity.stateTypeId <= 0 || entity.description.isBlank()
                if (notValid) {
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    // TODO проверка UNIQUE
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}