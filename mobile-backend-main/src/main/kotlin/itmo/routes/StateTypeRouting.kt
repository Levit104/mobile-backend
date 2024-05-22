package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.StateTypeDAO
import itmo.plugins.client

// TODO: 10.04.2024
fun Route.stateTypeRouting() {
    route("state-types") {

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/state-types/$id")

                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.OK, response.body<StateTypeDAO>())
                } else {
                    call.respond(response.status, response.bodyAsText())
                }
            }

            call.respond(HttpStatusCode.BadRequest, "Нет id")
        }

        post {
            val stateType = call.receive<StateTypeDAO>()

            val response: HttpResponse = client.post("http://localhost:8080/state-types") {
                setBody(stateType)
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}