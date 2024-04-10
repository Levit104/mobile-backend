package itmo.dao

import itmo.models.User
import itmo.models.Users
import itmo.util.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class UserDAO : BasicDAO<User> {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id].value,
        login = row[Users.login],
        password = row[Users.password]
    )

    override suspend fun findAll(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun findById(id: Int): User? = dbQuery {
        Users.select(Users.id eq id).map(::resultRowToUser).singleOrNull()
    }

    override suspend fun insert(entity: User): Int = dbQuery {
        Users.insert {
            it[login] = entity.login
            it[password] = entity.password
        }[Users.id].value
    }

    suspend fun findByLogin(login: String): User? = dbQuery {
        Users.select(Users.login eq login).map(::resultRowToUser).singleOrNull()
    }
}
