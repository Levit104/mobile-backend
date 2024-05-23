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
        // FIXME не используется
        get {
            log("user get", "-1", "get all", "success")
            call.respond(dao.findAll())
        }
        get("{login}") {
            val login = call.parameters["login"]

            try {
                if (login == null) {
                    throw BadRequestException("Указан некорректный логин (null)")
                }

                val entity =
                    dao.findByLogin(login) ?: throw BadRequestException("Пользователь с логином $login не найден")

                log(
                    "GET /users/$login",
                    "${entity.id}",
                    "Получение пользователя #${entity.id} с login=$login",
                    "success"
                )

                call.respond(entity)

            } catch (e: BadRequestException) {
                log(
                    "GET /users/$login",
                    "-1",
                    "Ошибка при получении пользователя: ${e.message}",
                    "fail"
                )

                call.respond(HttpStatusCode.BadRequest, "Ошибка при получении пользователя: ${e.message}")
            }
        }
        post {
            try {
                val entity = call.receive<User>()

                val notValid = entity.login.isBlank() || entity.password.isBlank()
                if (notValid) {
                    throw BadRequestException("Некорректное значение одного или нескольких полей - login, password")
                }

                if (dao.findByLogin(entity.login) != null) {
                    throw BadRequestException("Пользователь с логином ${entity.login} уже существует")
                }

                log(
                    "POST /users",
                    "-1",
                    "Добавлен пользователь с login=${entity.login}",
                    "success"
                )

                call.respond(dao.insert(entity))

            } catch (e: BadRequestException) {
                log(
                    "POST /users",
                    "-1",
                    "Ошибка при добавлении пользователя: ${e.message}",
                    "fail"
                )

                call.respond(HttpStatusCode.BadRequest, "Ошибка при добавлении пользователя: ${e.message}")
            }
        }
    }
}