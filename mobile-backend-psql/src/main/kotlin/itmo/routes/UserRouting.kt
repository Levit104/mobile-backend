package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.UserDAO
import itmo.models.User
import itmo.util.log

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
                    log("user post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    // TODO проверка UNIQUE
                    log("user post", "-1", "Insert: " + entity.login, "success")
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                log("user post", "-1", "Необходимо заполнить все поля", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}