package itmo.routes

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import itmo.cache.model.RoomDAO
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RoomRoutingTest {
    private val mockEngine = MockEngine { request ->
        when (request.url.fullPath) {
            "/rooms" -> {
                respond(
                    content = Json.encodeToString(listOf(RoomDAO(1, "kek", 1), RoomDAO(2, "kek2", 2))),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
            "/rooms/1" -> {
                respond(
                    content = Json.encodeToString(RoomDAO(1, "kek", 1)),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
            else -> {
                respond(
                    content = "Not found",
                    status = HttpStatusCode.NotFound,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString())
                )
            }
        }
    }

    fun getClient(): HttpClient {
        return HttpClient(mockEngine) {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json(Json {
                    explicitNulls = false
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }
    @Test
    fun testGetRoomRouting() = testApplication {
        val client = getClient()
        val response = client.get("/rooms")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf(RoomDAO(1, "kek", 1), RoomDAO(2, "kek2", 2)), response.body<List<RoomDAO>>())
    }
    @Test
    fun testGetByIdRoomRouting() = testApplication {
        val client = getClient()
        val response = client.get("/rooms/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(RoomDAO(1, "kek", 1), response.body<RoomDAO>())
    }

    @Test
    fun testBedRouting() = testApplication {
        val client = getClient()
        val response = client.get("/")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Not found", response.bodyAsText())
    }
}