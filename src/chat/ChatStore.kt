package argent.chat

import argent.util.toLocalDateTime
import com.grimsborn.database.DatabaseQueries
import com.grimsborn.database.asyncConnection
import javax.sql.DataSource

class ChatStore(private val db: DataSource) : DatabaseQueries {
    suspend fun getMessages(maxMessages: Int = 100): List<ChatMessage> {
        return db.asyncConnection {
            executeQuery(
                """
                SELECT id, sender_id, sender, message_text, created_date
                FROM chat_messages
                ORDER BY created_date DESC
                LIMIT ?
                """.trimIndent(),
                listOf(maxMessages),
                parseList { ChatMessage(it) }
            ).reversed()
        }
    }

    suspend fun addMessage(chatMessage: ChatMessage) {
        db.asyncConnection {
            executeUpdate(
                """
                    INSERT INTO chat_messages (
                        id,
                        sender_id,
                        sender,
                        message_text,
                        created_date
                    ) VALUES(?,?,?,?,?)
                """.trimIndent(),
                listOf(
                    chatMessage.id,
                    chatMessage.senderId,
                    chatMessage.sender,
                    chatMessage.messageText,
                    chatMessage.createdDate.toLocalDateTime(),
                )
            )
        }
    }
}
