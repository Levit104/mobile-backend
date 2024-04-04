package itmo.cache.model

import itmo.cache.RedisRepository

data class UserDAO (
    val login: String,
    val password: String
)

class UserRedisRepository : RedisRepository<UserDAO> {
    override suspend fun addItem(name: String, item: UserDAO, time: Long) {
        jedis.hset(name, "login", item.login)
        jedis.hset(name, "password", item.password)
        jedis.pexpire(name, time)
    }
}