package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.StatisticDAO
import itmo.models.Statistic
import itmo.util.log

// FIXME не работает POST, проблема с timestamp
fun Route.statisticRouting() {
    val dao = StatisticDAO()

    route("statistics") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                log("statistics get", "-1", "find all", "success")
                call.respond(dao.findAll())
            } else {
                val deviceId = call.request.queryParameters["deviceId"]?.toIntOrNull()

                if (deviceId == null || deviceId <= 0) {
                    log("statistics get", "-1", "Не указано устройство", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Не указано устройство")
                } else {
                    log("statistics get", "-1", "get by deviceId: $deviceId", "success")
                    call.respond(dao.findAllByDevice(deviceId))
                }
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Statistic? = dao.findById(id)
                if (entity == null) {
                    log("statistics get id", "-1", "Статистика с id=$id не найдено", "fail")
                    call.respond(HttpStatusCode.NotFound, "Статистика с id=$id не найдено")
                } else {
                    log("statistics get id", "-1", "get statistics by id $id", "success")
                    call.respond(entity)
                }
            }
            log("statistics get id", "-1", "no id", "fail")
        }
        post {
            try {
                val entity = call.receive<Statistic>()
                val notValid = entity.deviceId <= 0
                if (notValid) {
                    log("statistics post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    log("statistics post", "-1", "insert statistics", "success")
                    call.respond(dao.insert(entity))
                }
            } catch (e: BadRequestException) {
                log("statistics post", "-1", "BadRequestException", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}