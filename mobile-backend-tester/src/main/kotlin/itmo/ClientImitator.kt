package itmo

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import itmo.models.Room
import itmo.models.User
import itmo.util.log

class ClientImitator(private val id: Int) {
    private val login: String = "user$id"
    private val password: String = "password$id"
    private val roomName: String = "room_${id}_${(0..1000).random()}"
    private lateinit var jwt: String

    suspend fun init() {
        signUp()
        signIn()
        addRoom()
    }

    private suspend fun sendPost(url: String, body: Any, auth: Boolean = false) = client.post(url) {
        contentType(ContentType.Application.Json)
        setBody(body)
        if (auth) bearerAuth(jwt)
    }

    private suspend fun signUp() {
        val response: HttpResponse = sendPost("http://localhost:8082/signUp", User(login, password))
        log("signUp post", "$id", "Отправлен запрос на регистрацию", "success")
        println("reg $id")
        println("${response.status}: ${response.bodyAsText()}")
    }

    private suspend fun signIn() {
        val response: HttpResponse = sendPost("http://localhost:8082/signIn", User(login, password))
        log("signIn post", "$id", "Отправлен запрос на вход", "success")
        println("login $id")
        if (response.status == HttpStatusCode.OK) {
            jwt = response.bodyAsText()
            println("Imitator $id: пользователь авторизован")
        } else {
            println("Imitator $id: ${response.bodyAsText()}")
        }

    }

    private suspend fun addRoom() {
        val response: HttpResponse = sendPost("http://localhost:8082/rooms", Room(roomName, id), true)
        log("addRoom post", "$id", "Отправлен запрос на создание комнаты", "success")
        println("${response.status}: ${response.bodyAsText()}")
    }

}