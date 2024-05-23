package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.*
import itmo.plugins.client
import itmo.plugins.userRedisRepository
import itmo.util.log
import itmo.util.parseClaim
import itmo.util.sendPost

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
                log("devices get cash", userId, "Устройства получены из кэша", "success")
                call.respond(deviceRedisRepository.getItemsByUser(userId))
            } else {
                val response: HttpResponse = client.get("http://localhost:8080/devices") {
                    url {
                        parameters.append("userId", userId)
                    }
                }
                if (response.status == HttpStatusCode.OK) {
                    log("devices get", userId, "Устройства получены успешно", "success")
                    call.respond(response.body<List<DeviceDAO>>())
                } else {
                    log("devices get", userId, "Ошибочка, какая хз", "fail")
                    call.respond(HttpStatusCode.NoContent, "Ошибочка, какая хз")
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
                log("devices get id cash", userId, "Устройство получено из кэша id $id", "success")
                val actions = actionRedisRepository.getItemsByDeviceTypeId(device.typeId.toString())
                log("devices get id cash", userId, "Действия получены из кэша id $id", "success")
                val states = stateRedisRepository.getItemsByDeviceId(id.toString())
                log("devices get id cash", userId, "Состояния получены из кэша id $id", "success")
                call.respond(DeviceInfo(device, actions, states))
            } else if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/devices/$id") {
                    url {
                        parameters.append("userId", userId)
                    }
                }

                if (response.status == HttpStatusCode.OK) {
                    log("devices get id", userId, "Устройство получено успешно id $id", "success")
                    val deviceInfo = response.body<DeviceInfo>()
                    addDeviceToRedis(deviceInfo)
                    call.respond(deviceInfo)
                }
            } else {
                log("devices get id", userId, "Устройство не существует или у вас нет доступа!", "fail")
                call.respond(HttpStatusCode.Forbidden, "Устройство не существует или у вас нет доступа!")
            }
        }

        post {
            val device = call.receive<DeviceDAO>()

            val userId = parseClaim<String>("userId", call)

            val response: HttpResponse = sendPost(
                "http://localhost:8080/devices",
                DeviceDAO(null, device.name, device.typeId, device.roomId, userId.toLong())
            )

            if (response.status == HttpStatusCode.OK) {
                log("devices post", userId, "Устройство успешно добавлено! ${device.name}", "success")
                val deviceInfo = response.body<DeviceInfo>()
                addDeviceToRedis(deviceInfo)
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                log("devices post", userId, "Произошла ошибка при добавлении устройства ${device.name}", "fail")
                call.respond(HttpStatusCode.BadRequest, "Произошла ошибка при добавлении устройства")
            }
        }

        delete {
            val userId = parseClaim<String>("userId", call)

            val deviceId = call.request.queryParameters["deviceId"]?.toIntOrNull()

            if (deviceId !== null) {
                val response: HttpResponse = client.delete("http://localhost:8080/devices") {
                    url {
                        parameters.append("deviceId", deviceId.toString())
                    }
                }

                if (response.status == HttpStatusCode.OK) {
                    log("devices delete", userId, "Успешно удалилось действие", "success")
                    call.respond(HttpStatusCode.OK, "Успешно удалилось")
                } else {
                    log("devices delete", userId, "Не удалось удалить $deviceId", "fail")
                    call.respond(HttpStatusCode.NoContent, "Ошибочка, какая хз")
                }
            } else {
                log("devices delete", userId, "Нет id", "fail")
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }
    }
}