import argent.api.ApiController
import argent.data.checklists.ChecklistDataStore
import argent.server.DataBases
import argent.server.main
import argent.util.runMigrations
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking

interface ApplicationContext {
    val checklistDataStore: ChecklistDataStore
    val apiController: ApiController
    fun <T> testMain(callback: TestApplicationEngine.() -> T): T {
        return withTestApplication({
            main()
        }) { callback() }
    }
}

val DefaultApplicationContext = object: ApplicationContext {
    override val checklistDataStore = ChecklistDataStore(DataBases.Argent.database)
    override val apiController = ApiController

    init {
        runMigrations(DataBases.Argent.dataSource)
    }
}

interface ApplicationTest {
    fun <T: Any> withAppContext(context: ApplicationContext = DefaultApplicationContext,block: suspend ApplicationContext.() -> T): T {
        return runBlocking { context.run { block() } }
    }

    fun <T> testApp(context: ApplicationContext = DefaultApplicationContext,block: suspend ApplicationContext.() -> T) {
        runBlocking { context.run { block() } }
    }
}