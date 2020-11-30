@file:UseSerializers(UUIDSerializer::class)


package argent.api

import argent.data.users.User
import argent.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID

@Serializable
class UserForSharing private constructor(val id: UUID, val name: String){
    constructor(user: User): this(user.id, user.name)
}
