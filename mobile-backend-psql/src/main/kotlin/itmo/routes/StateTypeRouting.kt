package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.StateTypeDAO
import itmo.models.StateType

fun Route.stateTypeRouting() {
    val dao = StateTypeDAO()

    route("state-types") {
        get {
            call.respond(dao.findAll())
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: StateType? = dao.findById(id)
                if (entity == null) {
                    call.respond(HttpStatusCode.NotFound, "Тип состояния с id=$id не найден")
                } else {
                    call.respond(entity)
                }
            }
        }
        post {
            try {
                val entity = call.receive<StateType>()
                val notValid = entity.name.isBlank() || entity.description.isBlank()
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