package itmo.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.Config
import itmo.routes.deviceRouting
import java.util.*




fun Application.configureRouting() {
        routing {
        authenticate {
            deviceRouting()
        }
        post("/login") {
            val user = call.receive<User>()
            if ((user.username == "test") && (user.password == "test")) {
                val token = JWT.create()
                    .withAudience(Config.AUDIENCE.toString())
                    .withIssuer(Config.ISSUER.toString())
                    .withClaim("username", user.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                    .sign(Algorithm.HMAC256(Config.SECRET.toString()))
                call.respond(token)
            }
        }
    }
}
