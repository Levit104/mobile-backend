package itmo.routes

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import itmo.cache.model.ActionDAO
import itmo.cache.model.RoomDAO
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ActionRoutingTest {
    private val mockEngine = MockEngine { request ->
        when (request.url.fullPath) {
            "/actions" -> {
                respond(
                    content = Json.encodeToString(listOf(ActionDAO(1, 2, "вкл"), ActionDAO(2, 2, "выкл"))),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
            "/actions/1" -> {
                respond(
                    content = Json.encodeToString(ActionDAO(1, 2, "вкл")),
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
            install(ContentNegotiation) {
                json(Json {
                    explicitNulls = false
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }
    @Test
    fun testGetActionRouting() = testApplication {
        val client = getClient()
        val response = client.get("/actions")
        Assertions.assertEquals(HttpStatusCode.OK, response.status)
        Assertions.assertEquals(listOf(ActionDAO(1, 2, "вкл"), ActionDAO(2, 2, "выкл")), response.body<List<ActionDAO>>())
    }
    @Test
    fun testGetByIdActionRouting() = testApplication {
        val client = getClient()
        val response = client.get("/actions/1")
        Assertions.assertEquals(HttpStatusCode.OK, response.status)
        Assertions.assertEquals(ActionDAO(1, 2, "вкл"), response.body<ActionDAO>())
    }

    @Test
    fun testBedRouting() = testApplication {
        val client = getClient()
        val response = client.get("/")
        Assertions.assertEquals(HttpStatusCode.NotFound, response.status)
        Assertions.assertEquals("Not found", response.bodyAsText())
    }
}