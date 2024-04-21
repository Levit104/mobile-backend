package itmo.plugins

import io.ktor.server.application.*
import itmo.models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/mobile",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "1112"
    )
    transaction(database) {
        SchemaUtils.create(Users)
        SchemaUtils.create(Rooms)
        SchemaUtils.create(DeviceTypes)
        SchemaUtils.create(Devices)
        SchemaUtils.create(Notifications)
        SchemaUtils.create(Statistics)
        SchemaUtils.create(StateTypes)
        SchemaUtils.create(States)
        SchemaUtils.create(ActionTypes)
        SchemaUtils.create(Actions)
        SchemaUtils.create(Conditions)
        SchemaUtils.create(Scripts)
    }
}