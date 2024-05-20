package itmo

import io.ktor.client.network.sockets.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import itmo.models.User
import itmo.util.log
import org.slf4j.Logger

class ClientImitator(private val id: Int) {
    private val login : String = "user$id"
    private val password : String = "password$id"
    lateinit var jwt : String

    suspend fun init() {
        signUp()
        signIn()
    }

    private suspend fun signUp() {
            val response: HttpResponse = client.post("http://localhost:8082/signUp") {
                contentType(ContentType.Application.Json)
                setBody(User(login, password))
            }
        log("signUp post", "$id", "Отправлен запрос на регистрацию", "success")
        println("reg $id")
//        if (response.status == HttpStatusCode.OK)
//            println("Imitator $id: пользователь зарегистрирован")
//        else
//            println("Imitator $id: пользователь уже существует")
    }

    private suspend fun signIn() {
            val response: HttpResponse = client.post("http://localhost:8082/signIn") {
                contentType(ContentType.Application.Json)
                setBody(User(login, password))
            }

        log("signIn post", "$id", "Отправлен запрос на вход", "success")
            println("login $id")
//        if (response.status == HttpStatusCode.OK) {
//            jwt = response.bodyAsText()
//            println("Imitator $id: пользователь авторизован")
//        } else
//            println("Imitator $id: ${response.bodyAsText()}")
    }
}