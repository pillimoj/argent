@file:Suppress("unused")

package argent.util.database

import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.UUID

fun ResultSet.getLocalDateTime(columnName: String) = this.getObject(columnName, LocalDateTime::class.java)!!
fun ResultSet.getLocalDateTimeOrNull(columnName: String): LocalDateTime? =
    this.getObject(columnName, LocalDateTime::class.java)

fun ResultSet.getUUID(columnName: String) = this.getObject(columnName, UUID::class.java)!!
fun ResultSet.getUUIDOrNull(columnName: String): UUID? = this.getObject(columnName, UUID::class.java)

fun ResultSet.getStringList(columnName: String) =
    (getArray(columnName).array as? Array<out Any?>)?.filterIsInstance<String>() ?: emptyList()

fun ResultSet.getUUIDList(columnName: String) =
    (getArray(columnName).array as? Array<out Any?>)?.filterIsInstance<UUID>() ?: emptyList()
