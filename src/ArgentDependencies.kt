package argent

import argent.api.ApiController
import argent.checklists.ChecklistDataStore
import argent.server.DataBases

object ArgentDependencies {
    val database = DataBases.Argent.database
    val store = ChecklistDataStore(database)
    val controller = ApiController(store)
}