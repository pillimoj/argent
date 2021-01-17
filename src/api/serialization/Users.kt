@file:UseSerializers(UUIDSerializer::class)

package argent.api.serialization

import argent.data.checklists.ChecklistAccessType
import argent.data.users.User
import argent.data.users.UserRole
import argent.util.UUIDSerializer
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID

@Serializable
private class AddUserReq(val id: UUID?, val userName: String, val email: String) {
    val value: User
        get() = User(
            id = id ?: UUID.randomUUID(),
            name = userName,
            email = email,
            role = UserRole.User
        )
}

@Serializable
class ShareRequest(val userId: UUID, val accessType: ChecklistAccessType) {
    companion object {
        suspend fun deserialize(call: ApplicationCall): ShareRequest = call.receive()
    }
}

suspend fun User.Companion.deserialize(call: ApplicationCall) = call.receive<AddUserReq>().value

@Serializable
class UserForSharing private constructor(val id: UUID, val name: String) {
    constructor(user: User) : this(user.id, user.name)
}
