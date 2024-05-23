package itmo.cache.model

import itmo.cache.RedisRepository
import kotlinx.serialization.Serializable

@Serializable
data class UserDAO(
    var id: Long?,
    val login: String,
    val password: String
)

class UserRedisRepository : RedisRepository<UserDAO, UserDAO> {

    override suspend fun getItem(userId: String): UserDAO {
        val con = jedis
        val map = con.hgetAll("user#$userId")
        con.close()
        return UserDAO(
            map["id"]!!.toLong(),
            map["login"]!!,
            map["password"]!!,
        )
    }

    override suspend fun isItemExists(username: String): Boolean {
        val con = jedis
        val isExists = con.exists("username#$username")
        con.close()
        return isExists
    }

    override suspend fun addItem(userId: String, item: UserDAO, time: Long) {
        val con = jedis
        con.hset("user#$userId", "id", item.id.toString())
        con.hset("user#$userId", "login", item.login)
        con.hset("user#$userId", "password", item.password)
        con.pexpire("user#$userId", time)
        con.setex("username#${item.login}", time, userId)
        con.close()
    }

    suspend fun getUserByLogin(login: String): UserDAO {
        val con = jedis
        val item = getItem(con.get("username#$login"))
        con.close()
        return item
    }
}