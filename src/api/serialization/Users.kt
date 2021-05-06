package argent.api.serialization

import argent.data.checklists.ChecklistAccessType
import argent.data.users.User
import argent.data.users.UserRole
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import java.util.UUID

private class AddUserReq(val id: UUID? = null, val userName: String, val email: String) {
    val value: User
        get() = User(
            user = id ?: UUID.randomUUID(),
            name = userName,
            email = email,
            role = UserRole.User
        )
}

class ShareRequest(val user: UUID, val accessType: ChecklistAccessType) {
    companion object {
        suspend fun deserialize(call: ApplicationCall): ShareRequest = call.receive()
    }
}

suspend fun User.Companion.deserialize(call: ApplicationCall) = call.receive<AddUserReq>().value

class UserForSharing private constructor(val id: UUID, val name: String) {
    constructor(user: User) : this(user.user, user.name)
}
