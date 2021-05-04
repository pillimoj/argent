package functional

import ApplicationTest
import Faker
import TestAuthDefaultUser
import argent.data.checklists.Checklist
import argent.data.checklists.ChecklistItem
import argent.data.users.User
import io.ktor.util.date.GMTDate
import org.junit.BeforeClass
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class ChecklistItemsTest : ApplicationTest {
    override val authenticatedUser: User = TestAuthDefaultUser

    @BeforeClass
    fun setUp() {
    }

    @Test
    fun seedDatabase() = testApp {
        val cl1 = Checklist(UUID.randomUUID(), Faker.elderScrolls().city())
        checklistDataStore.addChecklist(cl1, authenticatedUser)
        val cl2 = Checklist(UUID.randomUUID(), Faker.elderScrolls().city())
        checklistDataStore.addChecklist(cl2, authenticatedUser)
        (1..10).map {
            val item = ChecklistItem(UUID.randomUUID(), Faker.elderScrolls().dragon(), false, cl1.id, GMTDate())
            checklistDataStore.addItem(item)
            item
        }.random().let {
            checklistDataStore.setItemDone(it.checklistItem, true)
        }
        (1..5).map {
            val item = ChecklistItem(UUID.randomUUID(), Faker.elderScrolls().dragon(), false, cl2.id, GMTDate())
            checklistDataStore.addItem(item)
            item
        }.random().let {
            checklistDataStore.setItemDone(it.checklistItem, true)
        }
        checklistDataStore.clearDone(cl2.id)
        val items1 = checklistDataStore.getChecklistItems(cl1.id)
        val items2 = checklistDataStore.getChecklistItems(cl2.id)

        val (doneCount, notDoneCount) = items1.partition { it.done }.toList().map { it.size }
        Assertions.assertEquals(1, doneCount)
        Assertions.assertEquals(9, notDoneCount)
        Assertions.assertEquals(4, items2.size)
    }
}
