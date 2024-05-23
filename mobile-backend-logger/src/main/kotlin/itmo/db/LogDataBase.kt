package itmo.db

import itmo.model.MessageLogDao
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

class LogDataBase {
    private lateinit var connection: Connection
    private val batchSize = 1000
    private var currentBatchSize = 0
    private lateinit var preparedStatement: PreparedStatement

    fun initDataBase() {
        try {
            Class.forName("com.clickhouse.jdbc.ClickHouseDriver")
            connection = DriverManager.getConnection("jdbc:clickhouse://localhost:8123", "default", "")
            createTable()
            prepareStatement()


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
                dateNotify String,
                description String,
                status String
            ) ENGINE = MergeTree()
            ORDER BY project;
        """.trimIndent()

        statement.execute(sqlCreateTable)
        println("Таблица создана или уже существует.")
    }

    /*public fun insertLog(log: MessageLogDao) {
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
    }*/

    private fun prepareStatement() {
        preparedStatement = connection.prepareStatement(
            """
            INSERT INTO message_logs (hostname, project, userId, eventName, dateNotify, description, status) VALUES (?,?,?,?, ?,?,?);
        """.trimIndent()
        )
    }

    fun insertLog(log: MessageLogDao) {
        preparedStatement.setString(1, log.hostname)
        preparedStatement.setString(2, log.project)
        preparedStatement.setString(3, log.userId)
        preparedStatement.setString(4, log.eventName)
        preparedStatement.setString(5, log.date)
        preparedStatement.setString(6, log.description)
        preparedStatement.setString(7, log.status)
        preparedStatement.addBatch()

        if (currentBatchSize++ >= batchSize) {
            executeBatch()
        }
    }

    fun flush() {
        if (currentBatchSize > 0) {
            executeBatch()
        }
    }

    private fun executeBatch() {
        preparedStatement.executeBatch()
        currentBatchSize = 0
        prepareStatement()
    }
}