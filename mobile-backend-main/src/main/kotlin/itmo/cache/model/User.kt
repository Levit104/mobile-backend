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
        val map = jedis.hgetAll("user#$userId")
        return UserDAO(
            map["id"]!!.toLong(),
            map["login"]!!,
            map["password"]!!,
        )
    }

    override suspend fun isItemExists(username: String): Boolean {
        return jedis.exists("username#$username")
    }

    override suspend fun addItem(userId: String, item: UserDAO, time: Long) {
        jedis.hset("user#$userId", "id", item.id.toString())
        jedis.hset("user#$userId", "login", item.login)
        jedis.hset("user#$userId", "password", item.password)
        jedis.pexpire("user#$userId", time)
        jedis.setex("username#${item.login}", time, userId)
    }

    suspend fun getUserByLogin(login: String): UserDAO {
        return getItem(jedis.get("username#$login"))
    }
}