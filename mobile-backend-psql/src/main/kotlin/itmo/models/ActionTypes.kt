package itmo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class ActionType(
    val id: Int?,
    val stateTypeId: Int,
    val description: String,
    val parameterMode: Boolean
)

object ActionTypes : IntIdTable("action_type") {
    val stateTypeId = reference("state_type_id", StateTypes)
    val description = varchar("description", 128).uniqueIndex()
    val parameterMode = bool("parameter_mode")
}
