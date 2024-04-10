package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.UserDAO
import itmo.models.User

fun Route.userRouting() {
    val dao = UserDAO()

    route("users") {
        get {
            call.respond(dao.findAll())
        }
        get("{login}") {
            val login = call.parameters["login"]
            if (login != null) {
                val entity: User? = dao.findByLogin(login)
                if (entity == null) {
                    call.respond(HttpStatusCode.NotFound, "Пользователь с login=$login не найден")
                } else {
                    call.respond(entity)
                }
            }
        }
        post {
            try {
                val entity = call.receive<User>()
                val notValid = entity.login.isBlank() || entity.password.isBlank()
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