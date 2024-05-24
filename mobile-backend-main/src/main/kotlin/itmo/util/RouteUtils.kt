package itmo.util

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import itmo.plugins.client
import kotlinx.serialization.json.Json

suspend fun sendPost(url: String, body: Any) = client.post(url) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

suspend fun sendGet(url: String, paramName: String, paramValue: String) = client.get(url) {
    parameter(paramName, paramValue)
}

suspend fun sendDelete(url: String) = client.delete(url)

inline fun <reified T : Any> parseClaim(param: String, call: ApplicationCall): T {
    val principal = call.principal<JWTPrincipal>()
    return Json.decodeFromString<T>(principal!!.payload.getClaim(param).toString())
}