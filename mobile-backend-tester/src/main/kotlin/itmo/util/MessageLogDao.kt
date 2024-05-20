package itmo.util

import kotlinx.serialization.Serializable

@Serializable
data class MessageLogDao (
    val hostname: String,
    val project: String,
    val userId: String,
    val eventName: String,
    val date: String,
    val description: String,
    val status: String,
)