package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.ScriptDao
import itmo.plugins.client
import itmo.util.log
import itmo.util.parseClaim
import itmo.util.sendPost

// TODO: 10.04.2024
fun Route.scriptRouting() {
    route("scripts") {
        get {
            val userId = parseClaim<String>("userId", call)

            val response: HttpResponse = client.get("http://localhost:8080/scripts") {
                url {
                    parameters.append("userId", userId)
                }
            }
            if (response.status == HttpStatusCode.OK) {
                log("scripts get", userId, "Успешно получены сценарии пользователя", "success")
                call.respond(response.body<List<ScriptDao>>())
            } else {
                log("scripts get", userId, "Ошибка при получение сценариев пользователя", "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            val userId = parseClaim<String>("userId", call)

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/scripts/$id")

                if (response.status == HttpStatusCode.OK) {
                    log("scripts get id", userId, "Успешно получен сценарий $id", "success")
                    call.respond(HttpStatusCode.OK, response.body<ScriptDao>())
                } else {
                    log("scripts get id", userId, "Ошибка при получение сценария $id", "fail")
                    call.respond(response.status, response.bodyAsText())
                }
            } else {
                log("scripts get id", userId, "Нет id", "fail")
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }
        post {
            val script = call.receive<ScriptDao>()

            val userId = parseClaim<String>("userId", call)

            val response: HttpResponse = sendPost("http://localhost:8080/scripts", script)

            if (response.status == HttpStatusCode.OK) {
                log("scripts post", userId, "Успешно добавлен сценарий ${response.bodyAsText()}", "success")
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                log("scripts post", userId, "Ошибка при добавление сценария", "fail")
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}