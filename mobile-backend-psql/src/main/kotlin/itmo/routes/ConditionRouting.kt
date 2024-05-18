package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.ConditionDAO
import itmo.models.Condition
import itmo.util.log

fun Route.conditionRouting() {
    val dao = ConditionDAO()

    route("conditions") {
        get {
            log("conditions get", "-1", "get all", "success")
            call.respond(dao.findAll())
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Condition? = dao.findById(id)
                if (entity == null) {
                    log("conditions get id", "-1", "Состояние с id=$id не найдено", "fail")
                    call.respond(HttpStatusCode.NotFound, "Состояние с id=$id не найдено")
                } else {
                    log("conditions get id", "-1", "get conditions ${entity.id}", "success")
                    call.respond(entity)
                }
            }
            log("conditions get id", "-1", "no id", "fail")
        }
        post {
            try {
                val entity = call.receive<Condition>()
                val notValid = entity.description.isBlank()
                if (notValid) {
                    log("conditions post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    // TODO проверка UNIQUE
                    log("conditions post", "-1", "Insert condition: ${entity.description}", "success")
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                log("conditions post", "-1", "BadRequestException", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}