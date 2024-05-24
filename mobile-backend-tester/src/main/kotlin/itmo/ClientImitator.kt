package itmo

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import itmo.models.*
import itmo.util.log
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ClientImitator(private val id: Int) {
    private val login: String = "user$id"
    private val password: String = "password$id"
    private lateinit var jwt: String
    private val functionList = listOf(
        ::getRooms,
        ::getDevices,
        ::getDeviceTypes,
        ::getDeviceInfo,
        ::executeAction,
        ::addRoom,
        ::addDevice,
        ::deleteRoom,
        ::deleteDevice
    )

    suspend fun init() {
        println("Up $id")
        signUp()

        println("In $id")
        signIn()

        repeat((3..5).random()) { idx ->
            println("$id $idx")
            val function = functionList.random()
            try {
                function()
            } catch (e: NoTransformationFoundException) { }
        }
    }

    private suspend fun sendPost(url: String, body: Any, auth: Boolean = false) = client.post(url) {
        contentType(ContentType.Application.Json)
        setBody(body)
        if (auth) {
            bearerAuth(jwt)
        }
    }

    private suspend fun sendGet(url: String, auth: Boolean = false) = client.get(url) {
        if (auth) {
            bearerAuth(jwt)
        }
    }

    private suspend fun sendDelete(url: String, auth: Boolean = false) =
        client.delete(url) {
            if (auth) {
                bearerAuth(jwt)
            }
        }

    private suspend fun signUp() {
        log("signUp post", "$id", "Отправлен запрос на регистрацию", "success")
        sendPost("http://localhost:8082/signUp", User(login, password))
    }

    private suspend fun signIn() {
        log("signIn post", "$id", "Отправлен запрос на вход", "success")
        val response: HttpResponse = sendPost("http://localhost:8082/signIn", User(login, password))
        if (response.status == HttpStatusCode.OK) {
            Mutex().withLock {
                jwt = response.bodyAsText()
            }
        }
    }

    private suspend fun addRoom(): Int? {
        log("addRoom post", "$id", "Отправлен запрос на создание комнаты", "success")
        val name = "room_${id}_${(0..1000).random()}"
        val response: HttpResponse = sendPost("http://localhost:8082/rooms", Room(name, id), true)

        if (response.status != HttpStatusCode.OK) {
            return null
        }

        return response.bodyAsText().toInt()
    }

    private suspend fun addDevice() {
        val rooms: List<Room> = getRooms()
        val roomId: Int? = if (rooms.isEmpty()) {
            addRoom()
        } else {
            rooms.random().id
        }

        if (roomId == null) {
            return
        }

        val typeId: Int = getDeviceTypes().random().id

        val name = "device_${id}_${(0..1000).random()}"

        log("addRoom post", "$id", "Отправлен запрос на создание девайса", "success")
        sendPost("http://localhost:8082/devices", Device(name, typeId, roomId, id), true)
    }

    private suspend inline fun <reified T> getObjects(url: String, logEvent: String, logDescription: String): List<T> {
        log(logEvent, "$id", logDescription, "success")
        val response: HttpResponse = sendGet(url, true)
        return response.body<List<T>>()
    }

    private suspend fun getRooms(): List<Room> = getObjects(
        "http://localhost:8082/rooms",
        "getRooms",
        "Отправлен запрос на получение комнат"
    )

    private suspend fun getDevices(): List<Device> = getObjects(
        "http://localhost:8082/devices",
        "getDevices",
        "Отправлен запрос на получение девайсов"
    )

    private suspend fun getDeviceTypes(): List<DeviceType> = getObjects(
        "http://localhost:8082/device-types",
        "getDeviceTypes",
        "Отправлен запрос на получение типов девайсов"
    )

    private suspend fun getDeviceInfo(): DeviceInfo? {
        val devices = getDevices()

        if (devices.isEmpty()) {
            return null
        }

        val deviceId = devices.random().id
        log("getDeviceInfo", "$id", "Отправлен запрос на получение девайса $deviceId", "success")
        val response: HttpResponse = sendGet("http://localhost:8082/devices/$deviceId", true)
        return response.body<DeviceInfo>()
    }

    private suspend fun executeAction() {
        val deviceInfo = getDeviceInfo()

        if (deviceInfo == null || deviceInfo.actions.isEmpty() || deviceInfo.states.isEmpty()) {
            return
        }

        val action = deviceInfo.actions.random()

        val paramValue = if (action.parameterMode) {
            (0..1000).random().toString()
        } else {
            ((deviceInfo.states.firstOrNull { it.actionId == action.id }?.value?.toInt() ?: return) + 1 % 2).toString()
        }

        log("executeAction", "$id", "Отправлен запрос на выполнение действия ${action.name}", "success")
        sendPost("http://localhost:8082/actions", ActionRequest(action.id, deviceInfo.device.id!!, paramValue), true)
    }

    private suspend fun deleteRoom() {
        val rooms = getRooms()

        if (rooms.isEmpty()) {
            return
        }

        val roomId = rooms.random().id!!

        log("deleteRoom", "$id", "Отправлен запрос на удаление комнаты $roomId", "success")
        sendDelete("http://localhost:8082/rooms/$roomId", true)
    }

    private suspend fun deleteDevice() {
        val devices = getDevices()

        if (devices.isEmpty()) {
            return
        }

        val deviceId = devices.random().id!!

        log("deleteDevice", "$id", "Отправлен запрос на удаление девайса $deviceId", "success")
        sendDelete("http://localhost:8082/devices/$deviceId", true)
    }
}