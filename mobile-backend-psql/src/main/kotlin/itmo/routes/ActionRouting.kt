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
                log("GET /actions", "-1", "Получение всех действий", "success")
                call.respond(actionDAO.findAll())
            } else {
                val deviceTypeId = call.request.queryParameters["deviceTypeId"]?.toIntOrNull()

                try {
                    if (deviceTypeId == null || deviceTypeId <= 0) {
                        throw BadRequestException("Указан некорректный deviceTypeId=$deviceTypeId")
                    }

                    log(
                        "GET /actions?deviceTypeId=$deviceTypeId",
                        "-1",
                        "Получение всех действий по deviceTypeId=$deviceTypeId",
                        "success"
                    )

                    call.respond(actionDAO.findAllByDeviceType(deviceTypeId))

                } catch (e: BadRequestException) {
                    log(
                        "GET /actions?deviceTypeId=$deviceTypeId",
                        "-1",
                        "Ошибка при получении действий: ${e.message}",
                        "fail"
                    )
                    call.respond(HttpStatusCode.BadRequest, "Ошибка при получении действий: ${e.message}")
                }
            }
        }
        // FIXME не используется
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
                    throw BadRequestException("Некорректное значение одного или нескольких полей - id, deviceId, parameter")
                }

                val stateId = stateDAO.updateValueByDeviceIdAndActionId(entity.deviceId, entity.id, entity.parameter)
                if (stateId == 0) {
                    throw BadRequestException("Состояние с deviceId=${entity.deviceId} и actionId=${entity.id} не найдено")
                }

                log(
                    "POST /actions",
                    "-1",
                    "Выполнено действие #${entity.id} у девайса #${entity.deviceId}, " +
                            "состояние #$stateId изменено на ${entity.parameter}",
                    "success"
                )

                call.respond(stateId)

            } catch (e: BadRequestException) {
                log(
                    "POST /actions",
                    "-1",
                    "Ошибка при выполнении действия: ${e.message}",
                    "fail"
                )

                call.respond(HttpStatusCode.BadRequest, "Ошибка при выполнении действия: ${e.message}")
            }
        }
    }
}