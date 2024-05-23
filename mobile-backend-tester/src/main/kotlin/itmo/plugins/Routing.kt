package itmo.plugins

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.client
import itmo.models.User

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
            val response: HttpResponse = client.post("http://localhost:8080/users") {
                contentType(ContentType.Application.Json)
                setBody(User("user0", "password0"))
            }
            call.respondText(response.bodyAsText())
        }
    }
}
