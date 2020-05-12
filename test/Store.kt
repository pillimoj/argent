
import argent.api.dto.ChecklistItemReq
import argent.api.dto.ChecklistReq
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StoreTest: ApplicationTest {
    @Test
    fun seedDatabase() = testApp {
        val cl1 = checklistDataStore.addChecklist(ChecklistReq(Faker.elderScrolls().city()))
        val cl2 = checklistDataStore.addChecklist(ChecklistReq(Faker.elderScrolls().city()))
        (1..10).map { _ ->
            checklistDataStore.addItem(ChecklistItemReq(cl1.id, Faker.elderScrolls().dragon()))
        }.random().let {
            checklistDataStore.setItemDone(it.id.value, true)
        }
        (1..5).map { _ ->
            checklistDataStore.addItem(ChecklistItemReq(cl2.id, Faker.elderScrolls().dragon()))
        }.random().let {
            checklistDataStore.deleteItem(it.id.value)
        }
        val items1 = checklistDataStore.getChecklistWithItems(cl1.id)
        val items2 = checklistDataStore.getChecklistWithItems(cl2.id)

        val (doneCount, notDoneCount) = items1.items.partition { it.done }.toList().map { it.size }
        Assertions.assertEquals(1, doneCount)
        Assertions.assertEquals(9, notDoneCount)
        Assertions.assertEquals(4, items2.items.size)
    }
}