
package argent.data.users

import argent.data.checklists.ChecklistAccessType
import io.ktor.auth.Principal
import java.util.UUID

enum class UserRole {
    Admin,
    User
}

data class User(
    val user: UUID,
    val name: String,
    val email: String,
    val role: UserRole,
) : Principal {
    companion object
}

data class UserAccess(
    val checklist: UUID,
    val user: UUID,
    val name: String,
    val checklistAccessType: ChecklistAccessType,
)