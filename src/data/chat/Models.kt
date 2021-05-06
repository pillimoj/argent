package argent.data.chat

import argent.data.users.User
import argent.util.defaultObjectMapper
import io.ktor.http.cio.websocket.Frame
import java.time.Instant
import java.util.UUID

enum class ChatDataType {
    Messages,
    ActiveUsers
}

data class MessagesData(val messages: List<ChatMessage>, val dataType: ChatDataType = ChatDataType.Messages) {
    fun toFrame(): Frame.Text {
        return Frame.Text(defaultObjectMapper.writeValueAsString(this))
    }
}

data class ActiveUsersData(val activeUsers: List<String>, val dataType: ChatDataType = ChatDataType.ActiveUsers) {
    fun toFrame(): Frame.Text {
        return Frame.Text(defaultObjectMapper.writeValueAsString(this))
    }
}

data class ChatMessage(val id: UUID, val senderId: UUID, val sender: String, val messageText: String, val createdDate: Instant) {

    fun toFrame(): Frame.Text {
        return MessagesData(listOf(this)).toFrame()
    }

    companion object {
        fun newMessage(user: User, messageText: String) = ChatMessage(
            id = UUID.randomUUID(),
            senderId = user.user,
            sender = user.name.split(" ").first(),
            messageText = messageText,
            createdDate = Instant.now()
        )
    }
}
