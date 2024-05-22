package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.ActionDAO
import itmo.dao.StateDAO
import itmo.models.Action
import itmo.util.log
import kotlinx.serialization.Serializable

@Serializable
data class ActionRequest(
    val id: Int,
    val deviceId: Int,
    val parameter: String
)

fun Route.actionRouting() {
    val actionDAO = ActionDAO()
    val stateDAO = StateDAO()

    route("actions") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                log("actions get", "-1", "get all", "success")
                call.respond(actionDAO.findAll())
            }

            val deviceTypeId = call.request.queryParameters["deviceTypeId"]?.toIntOrNull()

            if (deviceTypeId == null || deviceTypeId <= 0) {
                log("actions get", "-1", "Не указан тип устройства", "fail")
                call.respond(HttpStatusCode.BadRequest, "Не указан тип устройства")
            } else {
                log("actions get", "-1", "DeviceTypeId: $deviceTypeId", "success")
                call.respond(actionDAO.findAllByDeviceType(deviceTypeId))
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Action? = actionDAO.findById(id)
                if (entity == null) {
                    log("actions get id", "-1", "Действие с id=$id не найдено", "fail")
                    call.respond(HttpStatusCode.NotFound, "Действие с id=$id не найдено")
                } else {
                    log("actions get id", "-1", "Get id=$id", "success")
                    call.respond(entity)
                }
            }
            log("actions get id", "-1", "Нет id", "fail")
        }
        post {
            try {
                val entity = call.receive<ActionRequest>()
                val notValid = entity.id <= 0 || entity.deviceId <= 0 || entity.parameter.isBlank()
                if (notValid) {
                    log("actions post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    val stateId = stateDAO.updateValueByDeviceIdAndActionId(entity.deviceId, entity.id, entity.parameter)
                    log("actions post", "-1", "Update state $stateId with parameter=${entity.parameter}", "success")
                    call.respond(stateId)
                }
            } catch (e: BadRequestException) {
                log("actions post", "-1", "BadRequestException", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
    }
}