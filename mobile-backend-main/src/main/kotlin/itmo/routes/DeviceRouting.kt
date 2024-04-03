package itmo.routes

import io.ktor.server.routing.*



fun Route.deviceRouting() {
    route("/devices") {
        get("/") {

        }
        get("/{id?}") {
            
        }
        post("/") {
            
        }
    }
}