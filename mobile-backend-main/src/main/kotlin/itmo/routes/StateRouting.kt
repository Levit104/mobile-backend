package itmo.routes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.StateDAO
import itmo.plugins.client
import itmo.util.sendPost

// TODO: 10.04.2024
fun Route.stateRouting() {
    route("states") {
        get {
            val response: HttpResponse = client.get("http://localhost:8080/states")

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, response.body<StateDAO>())
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id != null) {
                val response: HttpResponse = client.get("http://localhost:8080/states/$id")

                if (response.status == HttpStatusCode.OK) {
                    call.respond(HttpStatusCode.OK, response.body<StateDAO>())
                } else {
                    call.respond(response.status, response.bodyAsText())
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Нет id")
            }
        }
        post {
            val stateDAO = call.receive<StateDAO>()

            val response: HttpResponse = sendPost("http://localhost:8080/states", stateDAO)

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }

        put {
            val stateDAO = call.receive<StateDAO>()

            val response: HttpResponse = client.put("http://localhost:8080/states") {
                contentType(ContentType.Application.Json)
                setBody(stateDAO)
            }

            if (response.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, response.bodyAsText())
            } else {
                call.respond(response.status, response.bodyAsText())
            }
        }
    }
}