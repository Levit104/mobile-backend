package itmo.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

// TODO: 10.04.2024  
fun Route.roomRouting() {
    route("room") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val response: HttpResponse = client.post("http://localhost:8080/devices") {
                body = device
            }
            if (response.status == HttpStatusCode.OK) {
                deviceRedisRepository.addItem(device.id, device, 300000)

                call.respond(HttpStatusCode.OK, "Устройство успешно добавлено!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Произошла ошибка при добавлении устройства")
            }
        }

        get("{id}") {

        }

    }
}