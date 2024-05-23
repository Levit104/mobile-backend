package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.*
import itmo.plugins.userRedisRepository
import itmo.util.*

val deviceRedisRepository = DeviceRedisRepository()
val actionRedisRepository = ActionRedisRepository()
val stateRedisRepository = StateRedisRepository()

suspend fun addDeviceToRedis(deviceInfo: DeviceInfo) {
    deviceRedisRepository.addItem(deviceInfo.device.id.toString(), deviceInfo.device)
    deviceInfo.actions.forEach { action ->
        actionRedisRepository.addRelation(
            deviceInfo.device.id.toString(),
            action.id.toString()
        )
    }
    deviceInfo.states.forEach { state -> stateRedisRepository.addItem(state.id.toString(), state) }
}

// TODO: 10.04.2024  
fun Route.deviceRouting() {
    route("devices") {
        get {
            val username = parseClaim<String>("username", call)
            val userId = parseClaim<String>("userId", call)

            if (userRedisRepository.isItemExists(username) && deviceRedisRepository.isItemsExistsByUser(userId)) {
                log("GET /devices", userId, "Получение всех девайсов у пользователя #$userId из кэша", "success")
                call.respond(deviceRedisRepository.getItemsByUser(userId))
            } else {
                val response = sendGet("http://localhost:8080/devices", "userId", userId)

                if (response.status == HttpStatusCode.OK) {
                    log("GET /devices", userId, "Получение всех девайсов у пользователя #$userId из БД", "success")
                    call.respond(response.body<List<DeviceDAO>>())
                } else {
                    log("GET /devices", userId, response.bodyAsText(), "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            }
        }
        get("{id}") {
            val username = parseClaim<String>("username", call)
            val userId = parseClaim<String>("userId", call)
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null &&
                deviceRedisRepository.isItemExistsByUser(id.toString(), username) &&
                stateRedisRepository.isItemsExistsByDeviceId(id.toString()) &&
                actionRedisRepository.isItemsExistsByDeviceTypeId(deviceRedisRepository.getItem(id.toString()).typeId.toString())
            ) {
                val device = deviceRedisRepository.getItem(id.toString())
                log("GET /devices/$id", userId, "Получен девайс #$id из кэша", "success")
                val actions = actionRedisRepository.getItemsByDeviceTypeId(device.typeId.toString())
                log("GET /devices/$id", userId, "Получены действия из кэша", "success")
                val states = stateRedisRepository.getItemsByDeviceId(id.toString())
                log("GET /devices/$id", userId, "Получены состояния из кэша", "success")
                call.respond(DeviceInfo(device, actions, states))
            } else {
                val response = sendGet("http://localhost:8080/devices/$id", "userId", userId)

                if (response.status == HttpStatusCode.OK) {
                    log("GET /devices/$id", userId, "Получена информация о девайсе #$id из БД", "success")
                    val deviceInfo = response.body<DeviceInfo>()
                    addDeviceToRedis(deviceInfo)
                    call.respond(deviceInfo)
                } else {
                    log("GET /devices/$id", userId, response.bodyAsText(), "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            }
        }
        post {
            val device = call.receive<DeviceDAO>()

            val userId = parseClaim<String>("userId", call)

            val response = sendPost(
                "http://localhost:8080/devices",
                DeviceDAO(null, device.name, device.typeId, device.roomId, userId.toLong())
            )

            if (response.status == HttpStatusCode.OK) {
                log(
                    "POST /devices",
                    userId,
                    "Добавлен девайс с названием ${device.name} пользователю #${userId}",
                    "success"
                )
                val deviceInfo = response.body<DeviceInfo>()
                addDeviceToRedis(deviceInfo)
                call.respond(deviceInfo)
            } else {
                log("POST /devices", userId, response.bodyAsText(), "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }
        delete {
            val userId = parseClaim<String>("userId", call)
            val deviceId = call.request.queryParameters["deviceId"]?.toIntOrNull()
            val response = sendDelete("http://localhost:8080/devices", "deviceId", deviceId.toString())

            val logStatus = if (response.status == HttpStatusCode.OK) "success" else "fail"
            log("DELETE /devices?deviceId=$deviceId", userId, response.bodyAsText(), logStatus)
            call.respond(response.status, response.bodyAsText())
        }
    }
}