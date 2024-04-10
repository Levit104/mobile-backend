package itmo.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.cache.model.DeviceDAO
import itmo.cache.model.DeviceRedisRepository
import itmo.cache.model.UserDAO
import itmo.cache.model.UserRedisRepository


val deviceRedisRepository = DeviceRedisRepository()
fun Route.deviceRouting() {
    route("devices") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            call.respond(deviceRedisRepository.getItemsByUser(userRedisRepository.getUserByLogin(username)["id"].orEmpty()))
        }
        get("{id}") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null && deviceRedisRepository.isItemExistsByUser(id.toString(), username)) {
                call.respond(deviceRedisRepository.getItem(id.toString()))
            } else {
                call.respond(HttpStatusCode.Forbidden, "Устройство не существует или у вас нет доступа!")
            }
        }
        post {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val device = call.receive<DeviceDAO>()
            deviceRedisRepository.addItem("1", device, 300000)
            call.respond(HttpStatusCode.Created, "Устройство успешно добавлено!")
        }
    }
}