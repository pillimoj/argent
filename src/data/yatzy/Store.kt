package argent.data.yatzy

import argent.data.users.User
import argent.util.database.DatabaseQueries
import argent.util.database.asyncConnection
import javax.sql.DataSource

class GameDatastore(private val db: DataSource) : DatabaseQueries {
}
