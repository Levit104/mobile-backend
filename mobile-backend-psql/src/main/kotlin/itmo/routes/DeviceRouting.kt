package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.dao.*
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
    val roomDAO = RoomDAO()
    val typeDAO = DeviceTypeDAO()

    route("devices") {
        get {
            if (call.request.queryParameters.isEmpty()) {
                log("GET /devices", "-1", "Получение всех девайсов", "success")
                call.respond(deviceDAO.findAll())
            } else {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()

                try {
                    if (userId == null || userId <= 0) {
                        throw BadRequestException("Указан некорректный userId=$userId")
                    }

                    log(
                        "GET /devices?userId=$userId",
                        "$userId",
                        "Получение всех девайсов у пользователя #$userId",
                        "success"
                    )

                    call.respond(deviceDAO.findAllByUser(userId))

                } catch (e: BadRequestException) {
                    log(
                        "GET /devices?userId=$userId",
                        "-1",
                        "Ошибка при получении девайсов: ${e.message}",
                        "fail"
                    )

                    call.respond(HttpStatusCode.BadRequest, "Ошибка при получении девайсов: ${e.message}")
                }
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            try {
                if (id == null || id <= 0) {
                    throw BadRequestException("Указан некорректный id=$id")
                }

                val entity = deviceDAO.findById(id) ?: throw BadRequestException("Девайс #$id не найден")
                val actions = actionDAO.findAllByDeviceType(entity.typeId)
                val states = stateDAO.findAllByDevice(entity.id!!)
                val deviceInfo = DeviceInfo(entity, actions, states)

                log(
                    "GET /devices/$id",
                    "-1",
                    "Получена информация о девайсе #$id",
                    "success"
                )

                call.respond(deviceInfo)

            } catch (e: BadRequestException) {
                log(
                    "GET /devices/$id",
                    "-1",
                    "Ошибка при получении девайса: ${e.message}",
                    "fail"
                )

                call.respond(HttpStatusCode.BadRequest, "Ошибка при получении девайса #$id: ${e.message}")
            }
        }
        post {
            try {
                val entity = call.receive<Device>()

                val notValid = entity.name.isBlank() || entity.typeId <= 0 || entity.userId <= 0
                if (notValid) {
                    throw BadRequestException("Некорректное значение одного или нескольких полей - name, typeId, userId")
                }

                if (entity.roomId != null && roomDAO.findById(entity.roomId) == null) {
                    throw BadRequestException("Комната #${entity.roomId} не существует")
                }

                if (typeDAO.findById(entity.typeId) == null) {
                    throw BadRequestException("Тип девайса #${entity.typeId} не существует")
                }

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

                log(
                    "POST /devices",
                    "${entity.userId}",
                    "Добавлен девайс #$deviceId",
                    "success"
                )

                call.respond(deviceInfo)

            } catch (e: BadRequestException) {
                log(
                    "POST /devices",
                    "-1",
                    "Ошибка при добавлении девайса: ${e.message}",
                    "fail"
                )

                call.respond(HttpStatusCode.BadRequest, "Ошибка при добавлении девайса: ${e.message}")
            }
        }
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            try {
                if (id == null || id <= 0) {
                    throw BadRequestException("Указан некорректный id=$id")
                }

                if (deviceDAO.findById(id) == null) {
                    throw BadRequestException("Девайс #$id не существует")
                }

                deviceDAO.deleteById(id)
                log("DELETE /devices/$id", "-1", "Девайс #$id удалён", "success")
                call.respond("Девайс #$id удалён")

            } catch (e: BadRequestException) {
                log(
                    "DELETE /devices/$id",
                    "-1",
                    "Ошибка при удалении девайса: ${e.message}",
                    "fail"
                )

                call.respond(HttpStatusCode.BadRequest, "Ошибка при удалении девайса: ${e.message}")
            }
        }
    }
}