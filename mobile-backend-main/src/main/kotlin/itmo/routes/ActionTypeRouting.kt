package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.ActionTypeDAO
import itmo.cache.model.DeviceTypeDAO
import itmo.cache.model.RoomDAO
import itmo.plugins.client

// TODO: 10.04.2024
fun Route.actionTypeRouting() {
    route("action-types") {

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/action-types/$id")

                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.OK, response.body<ActionTypeDAO>())
                } else {
                    call.respond(response.status, response.bodyAsText())
                }
            }

            call.respond(HttpStatusCode.BadRequest, "Нет id")
        }

        post {
            val actionTypeDAO = call.receive<ActionTypeDAO>()

            val response: HttpResponse = client.post("http://localhost:8080/action-types") {
                setBody(actionTypeDAO)
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, "Тип действия успешно добавлен!")
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}