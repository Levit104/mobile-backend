package itmo.plugins

import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import itmo.models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    /*
    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/mobile",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "qwerty123"
    )
     */

    val dataSource = HikariDataSource().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/mobile"
        driverClassName = "org.postgresql.Driver"
        username = "postgres"
        password = "qwerty123"
        maximumPoolSize = 8
    }

    val database = Database.connect(dataSource)

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
