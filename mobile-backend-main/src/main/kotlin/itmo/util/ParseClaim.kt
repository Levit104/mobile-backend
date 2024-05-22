package itmo.util

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.json.Json

inline fun<reified T : Any> parseClaim(param: String, call: ApplicationCall): T {
    val principal = call.principal<JWTPrincipal>()
    return Json.decodeFromString<T>(principal!!.payload.getClaim(param).toString())
}