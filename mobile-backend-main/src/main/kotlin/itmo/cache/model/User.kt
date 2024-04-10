package itmo.cache.model

import itmo.cache.RedisRepository
import kotlinx.serialization.Serializable

@Serializable
data class UserDAO (
    var id : Long?,
    val username: String,
    val password: String
)

class UserRedisRepository : RedisRepository<UserDAO, Map<String, String>> {

    override suspend fun getItem(userId: String): Map<String, String> {
        return jedis.hgetAll("user#$userId")
    }

    override suspend fun isItemExists(username: String): Boolean {
        return jedis.exists("username#$username")
    }

    override suspend fun addItem(userId: String, item: UserDAO, time: Long) {
        jedis.hset("user#$userId", "id", item.id.toString())
        jedis.hset("user#$userId", "login", item.username)
        jedis.hset("user#$userId", "password", item.password)
        jedis.pexpire("user#$userId", time)
        jedis.setex("username#${item.username}", time, userId)
    }

    suspend fun getUserByLogin(login: String) : Map<String, String>{
        return getItem(jedis.get("username#$login"))
    }
}