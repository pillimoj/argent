@file:UseSerializers(GMTDateSerializer::class, UUIDSerializer::class)
package argent.chat

import argent.data.users.User
import argent.util.GMTDateSerializer
import argent.util.UUIDSerializer
import argent.util.toGMTDate
import com.grimsborn.database.getLocalDateTime
import com.grimsborn.database.getUUID
import io.ktor.http.cio.websocket.Frame
import io.ktor.util.date.GMTDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.sql.ResultSet
import java.util.UUID

enum class ChatDataType {
    Messages,
    ActiveUsers
}

@Serializable
data class MessagesData(val messages: List<ChatMessage>, val dataType: ChatDataType = ChatDataType.Messages) {
    fun toFrame(): Frame.Text {
        return Frame.Text(Json { encodeDefaults = true }.encodeToString(this))
    }
}

@Serializable
data class ActiveUsersData(val activeUsers: List<String>, val dataType: ChatDataType = ChatDataType.ActiveUsers) {
    fun toFrame(): Frame.Text {
        return Frame.Text(Json { encodeDefaults = true }.encodeToString(this))
    }
}

@Serializable
data class ChatMessage(val id: UUID, val senderId: UUID, val sender: String, val messageText: String, val createdDate: GMTDate) {
    constructor(rs: ResultSet) : this(
        id = rs.getUUID("id"),
        senderId = rs.getUUID("sender_id"),
        sender = rs.getString("sender"),
        messageText = rs.getString("message_text"),
        createdDate = rs.getLocalDateTime("created_date").toGMTDate(),
    )

    fun toFrame(): Frame.Text {
        return MessagesData(listOf(this)).toFrame()
    }

    companion object {
        fun newMessage(user: User, messageText: String) = ChatMessage(
            id = UUID.randomUUID(),
            senderId = user.id,
            sender = user.name.split(" ").first(),
            messageText = messageText,
            createdDate = GMTDate()
        )
    }
}
