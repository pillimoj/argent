package argent.checklists

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.util.UUID


internal object Checklists : UUIDTable("checklists") {
    val name = text("name")
}

internal object ChecklistItems : UUIDTable("checklistitems") {
    val title = text("title")
    val done = bool("done")
    val createdAt = datetime("created_at")
    val checklist = reference("checklist", Checklists)
}

class Checklist(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object: UUIDEntityClass<Checklist>(Checklists)
    var name by Checklists.name
    val items by ChecklistItem referrersOn ChecklistItems.checklist
}

class ChecklistItem(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<ChecklistItem>(ChecklistItems)
    var title by ChecklistItems.title
    var done by ChecklistItems.done
    var createdAt by ChecklistItems.createdAt
    var checklist by Checklist referencedOn ChecklistItems.checklist
}
