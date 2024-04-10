package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.StatisticDAO
import itmo.models.Statistic

// FIXME не работает POST, проблема с timestamp
fun Route.statisticRouting() {
    val dao = StatisticDAO()

    route("statistics") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                call.respond(dao.findAll())
            }

            val deviceId = call.request.queryParameters["deviceId"]?.toIntOrNull()

            if (deviceId == null || deviceId <= 0) {
                call.respond(HttpStatusCode.BadRequest, "Не указано устройство")
            } else {
                call.respond(dao.findAllByDevice(deviceId))
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Statistic? = dao.findById(id)
                if (entity == null) {
                    call.respond(HttpStatusCode.NotFound, "Статистика с id=$id не найдено")
                } else {
                    call.respond(entity)
                }
            }
        }
        post {
            try {
                val entity = call.receive<Statistic>()
                val notValid = entity.deviceId <= 0
                if (notValid) {
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}