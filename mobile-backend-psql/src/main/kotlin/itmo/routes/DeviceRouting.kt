package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.ActionDAO
import itmo.dao.DeviceDAO
import itmo.dao.StateDAO
import itmo.models.Action
import itmo.models.Device
import itmo.models.State
import itmo.util.log
import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val device: Device,
    val actions: List<Action>,
    val states: List<State>
)

fun Route.deviceRouting() {
    val deviceDAO = DeviceDAO()
    val actionDAO = ActionDAO()
    val stateDAO = StateDAO()

    route("devices") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                log("devices get", "-1", "find all", "success")
                call.respond(deviceDAO.findAll())
            }

            val userId = call.request.queryParameters["userId"]?.toIntOrNull()

            if (userId == null || userId <= 0) {
                log("devices get", "-1", "Не указан пользователь", "fail")
                call.respond(HttpStatusCode.BadRequest, "Не указан пользователь")
            } else {
                log("devices get", "$userId", "devices by userId $userId", "success")
                call.respond(deviceDAO.findAllByUser(userId))
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null) {
                val entity: Device? = deviceDAO.findById(id)
                if (entity == null) {
                    log("devices get id", "-1", "Устройство с id=$id не найден", "fail")
                    call.respond(HttpStatusCode.NotFound, "Устройство с id=$id не найден")
                } else {
                    val actions = actionDAO.findAllByDeviceType(entity.typeId)
                    val states = stateDAO.findAllByDevice(entity.id!!)
                    val deviceInfo = DeviceInfo(entity, actions, states)
                    log("devices get id", "-1", "get device by id $id", "success")
                    call.respond(deviceInfo)
                }
            }
            log("devices get id", "-1", "no id", "fail")
        }
        post {
            try {
                val entity = call.receive<Device>()
                val notValid = entity.name.isBlank() || entity.typeId <= 0 || entity.userId <= 0
                if (notValid) {
                    log("devices post", "-1", "Необходимо заполнить все поля", "fail")
                    call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
                } else {
                    // TODO проверка UNIQUE у пользователя
                    val deviceId = deviceDAO.insert(entity)
                    val actions = actionDAO.findAllByDeviceType(entity.typeId)
                    val states = mutableListOf<State>()
                    actions.forEach {
                        val state = State(deviceId, it.id!!, "0")
                        val stateId = stateDAO.insert(state)
                        states.add(State(stateId, deviceId, it.id, "0")) // FIXME
                    }
                    val device = Device(deviceId, entity.name, entity.typeId, entity.roomId, entity.userId) // FIXME
                    val deviceInfo = DeviceInfo(device, actions, states)
                    log("devices post", "${entity.userId}", "Insert device name ${entity.name}", "success")
                    call.respond(deviceInfo)
                }
            } catch (e: BadRequestException) {
                log("devices post", "-1", "BadRequestException", "error")
                call.respond(HttpStatusCode.BadRequest, "Необходимо заполнить все поля")
            }
        }
        delete {
            val id = call.request.queryParameters["deviceId"]?.toIntOrNull()

            if (id == null || id <= 0) {
                log("device delete", "-1", "Не указан id", "fail")
                call.respond(HttpStatusCode.BadRequest, "Не указан id")
            } else {
                deviceDAO.deleteById(id)
                log("device delete", "-1", "Девайс $id удалён", "success")
                call.respond(HttpStatusCode.OK, "Девайс $id удалён")
            }
        }
    }
}