package itmo.dao

interface BasicDAO<T> {
    suspend fun findAll(): List<T>
    suspend fun findById(id: Int): T?
    suspend fun insert(entity: T): Int
}