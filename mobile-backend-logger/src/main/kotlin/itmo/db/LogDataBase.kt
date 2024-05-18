package itmo.db

import itmo.model.MessageLogDao
import java.sql.Connection
import java.sql.DriverManager

class LogDataBase {
    private lateinit var connection: Connection;

    fun initDataBase() {
        try {
            Class.forName("com.clickhouse.jdbc.ClickHouseDriver")
            connection = DriverManager.getConnection("jdbc:clickhouse://localhost:8123", "default", "")
            createTable()

            println("Connected successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Ошибка при работе с ClickHouse: ${e.message}")
        }
    }

    fun createTable() {
        val statement = connection.createStatement()
        val sqlCreateTable = """
            CREATE TABLE IF NOT EXISTS message_logs (
                hostname String,
                project String,
                userId String,
                eventName String,
                date DateTime,
                description String,
                status String
            ) ENGINE = MergeTree()
            ORDER BY project;
        """.trimIndent()

        statement.execute(sqlCreateTable)
        println("Таблица создана или уже существует.")
    }

    public fun insertLog(log: MessageLogDao) {
        val preparedStatement = connection.prepareStatement("""
        INSERT INTO message_logs (hostname, project, userId, eventName, date, description, status) VALUES (?,?,?,?, parseDateTimeBestEffort(?),?, ?);
    """.trimIndent())
        
        preparedStatement.setString(1, log.hostname)
        preparedStatement.setString(2, log.project)
        preparedStatement.setString(3, log.userId)
        preparedStatement.setString(4, log.eventName)
        preparedStatement.setString(5, log.date)
        preparedStatement.setString(6, log.description)
        preparedStatement.setString(7, log.status)

        preparedStatement.executeUpdate()
        println("Лог успешно добавлен.")
    }
}