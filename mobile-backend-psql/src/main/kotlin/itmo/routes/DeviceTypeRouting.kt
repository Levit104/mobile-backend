package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.DeviceTypeDAO
import itmo.models.DeviceType

fun Route.deviceTypeRouting() {
    val dao = DeviceTypeDAO()

    route("device-types") {
        get {
            call.respond(dao.findAll())
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: DeviceType? = dao.findById(id)
                if (entity == null) {
                    call.respond(HttpStatusCode.NotFound, "Тип устройства с id=$id не найден")
                } else {
                    call.respond(entity)
                }
            }
        }
        post {
            try {
                val entity = call.receive<DeviceType>()
                val notValid = entity.name.isBlank()
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
