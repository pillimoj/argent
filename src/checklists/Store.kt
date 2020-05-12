package argent.checklists


import argent.api.dto.ChecklistItemReq
import argent.api.dto.ChecklistItemRes
import argent.api.dto.ChecklistReq
import argent.api.dto.ChecklistRes
import argent.api.dto.ChecklistWithItemsRes
import argent.server.transaction
import argent.util.toGMTDate
import org.jetbrains.exposed.sql.Database
import java.time.LocalDateTime
import java.util.UUID

class ChecklistDataStore(private val db: Database) {

    suspend fun getChecklists(): List<ChecklistRes> {
        return db.transaction { Checklist.all().map { ChecklistRes(it.id.value, it.name) } }
    }

    suspend fun getChecklistWithItems(id: UUID): ChecklistWithItemsRes {
        return db.transaction {
            val checklist = Checklist[id]
            ChecklistWithItemsRes(
                checklist.id.value,
                checklist.name,
                checklist.items.map { ChecklistItemRes(it.id.value, it.title, it.done, it.createdAt.toGMTDate()) })
        }
    }

    suspend fun hasChecklist(id: UUID): Boolean {
        return db.transaction {
            Checklist.findById(id) != null
        }
    }

    suspend fun addChecklist(req: ChecklistReq): ChecklistRes {
        val cl = db.transaction {
            Checklist.new {
                name = req.name
            }
        }
        return ChecklistRes(cl.id.value, cl.name)
    }

    suspend fun addItem(req: ChecklistItemReq): ChecklistItem {
        return db.transaction {
            ChecklistItem.new {
                checklist = Checklist[req.checklist]
                title = req.title
                done = false
                createdAt = LocalDateTime.now()
            }
        }
    }

    suspend fun setItemDone(id: UUID, isDone: Boolean) {
        db.transaction {
            ChecklistItem[id].done = isDone
        }
    }

    suspend fun deleteItem(id: UUID) {
        db.transaction {
            ChecklistItem[id].delete()
        }
    }

    suspend fun deleteChecklist(id: UUID) {
        db.transaction {
            Checklist[id].delete()
        }
    }
}