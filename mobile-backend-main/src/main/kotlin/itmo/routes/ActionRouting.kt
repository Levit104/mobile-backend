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
import itmo.cache.model.ActionDAO
import itmo.cache.model.DeviceTypeDAO
import itmo.cache.model.RoomDAO
import itmo.cache.model.StateDAO
import itmo.plugins.client

// TODO: 10.04.2024
fun Route.actionRouting() {
    route("actions") {

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/actions/$id")

                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.OK, response.body<ActionDAO>())
                } else {
                    call.respond(response.status, response.bodyAsText())
                }
            }

            call.respond(HttpStatusCode.BadRequest, "Нет id")
        }


        post {
            val actionDAO = call.receive<ActionDAO>()

            val response: HttpResponse = client.post("http://localhost:8080/actions") {
                setBody(actionDAO)
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, "Действие успешно добавлено!")
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }


    }
}