@file:UseSerializers(UUIDSerializer::class)

package argent.data.users

import argent.data.checklists.ChecklistAccessType
import argent.util.UUIDSerializer
import argent.util.asEnum
import argent.util.getUUID
import io.ktor.auth.Principal
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.sql.ResultSet
import java.util.UUID

enum class UserRole {
    Admin,
    User
}

@Serializable
data class User(
    val id: UUID,
    val name: String,
    val email: String,
    val role: UserRole,
) : Principal {
    constructor(rs: ResultSet) : this(
        id = rs.getUUID("id"),
        name = rs.getString("name"),
        email = rs.getString("email"),
        role = rs.getString("role").asEnum<UserRole>()
    )
}

@Serializable
data class UserAccess(
    val id: UUID,
    val name: String,
    val checklistAccessType: ChecklistAccessType,
) : Principal {
    constructor(rs: ResultSet) : this(
        id = rs.getUUID("id"),
        name = rs.getString("name"),
        checklistAccessType = rs.getString("access_type").asEnum<ChecklistAccessType>()
    )
}
