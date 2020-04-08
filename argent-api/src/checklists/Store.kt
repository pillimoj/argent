package argent.checklists

import argent.api.ChecklistItemReq
import argent.api.ChecklistItemRes
import argent.api.ChecklistReq
import argent.api.ChecklistRes
import argent.api.ChecklistWithItemsRes
import argent.util.transaction
import org.jetbrains.exposed.sql.Database
import java.time.LocalDateTime
import java.util.UUID

class ChecklistDataStore(private val db: Database) {

    suspend fun getChecklists(): List<ChecklistRes> {
        return db.transaction { Checklist.all().map { ChecklistRes(it) } }
    }

    suspend fun getChecklistWithItems(id: UUID): ChecklistWithItemsRes {
        return db.transaction {
            val checklist = Checklist[id]
            ChecklistWithItemsRes(
                checklist.id.value,
                checklist.name,
                checklist.items.map { ChecklistItemRes(it) })
        }
    }

    suspend fun hasChecklist(id: UUID): Boolean{
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
        return ChecklistRes(cl)
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