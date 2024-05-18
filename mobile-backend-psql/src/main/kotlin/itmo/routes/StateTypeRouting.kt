package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.StateTypeDAO
import itmo.models.StateType
import itmo.util.log

fun Route.stateTypeRouting() {
    val dao = StateTypeDAO()

    route("state-types") {
        get {
            log("state-types get", "-1", "get all", "success")
            call.respond(dao.findAll())
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: StateType? = dao.findById(id)
                if (entity == null) {
                    log("state-types get id", "-1", "Тип состояния с id=$id не найден", "fail")
                    call.respond(HttpStatusCode.NotFound, "Тип состояния с id=$id не найден")
                } else {
                    log("state-types get id", "-1", "get by id $id", "success")
                    call.respond(entity)
                }
            }

            log("state-types get id", "-1", "no id", "fail")
        }
        post {
            try {
                val entity = call.receive<StateType>()
                val notValid = entity.name.isBlank() || entity.description.isBlank()
                if (notValid) {
                    log("state-types post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    // TODO проверка UNIQUE
                    log("state-types post", "-1", "insert state-types", "success")
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                log("state-types post", "-1", "BadRequestException", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}