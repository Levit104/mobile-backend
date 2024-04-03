package itmo.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import itmo.Config

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    authentication {
        jwt {
            realm = Config.REALM.toString()
            
            verifier(
                JWT
                    .require(Algorithm.HMAC256(Config.SECRET.toString()))
                    .withAudience(Config.AUDIENCE.toString())
                    .withIssuer(Config.ISSUER.toString())
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
