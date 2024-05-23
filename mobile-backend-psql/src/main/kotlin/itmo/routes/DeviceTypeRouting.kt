package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.DeviceTypeDAO
import itmo.models.DeviceType
import itmo.util.log

fun Route.deviceTypeRouting() {
    val dao = DeviceTypeDAO()

    route("device-types") {
        get {
            log("GET /device-types", "-1", "Получение всех типов девайсов", "success")
            call.respond(dao.findAll())
        }
        // FIXME не используется
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: DeviceType? = dao.findById(id)
                if (entity == null) {
                    log("device-types get id", "-1", "Тип устройства с id=$id не найден", "fail")
                    call.respond(HttpStatusCode.NotFound, "Тип устройства с id=$id не найден")
                } else {
                    log("device-types get id", "-1", "get device-types by id $id", "success")
                    call.respond(entity)
                }
            }
            log("device-types get id", "-1", "no id", "fail")
        }
        // FIXME не используется
        post {
            try {
                val entity = call.receive<DeviceType>()
                val notValid = entity.name.isBlank()
                if (notValid) {
                    log("device-types post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    // TODO проверка UNIQUE
                    log("device-types post", "-1", "insert device-types name ${entity.name}", "success")
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                log("device-types post", "-1", "BadRequestException", "fail")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}
