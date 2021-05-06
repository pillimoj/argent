package argent.data.chat

import argent.google.ArgentStore
import argent.google.await
import argent.google.parseList
import argent.google.storable

class ChatStore(private val db: ArgentStore) {
    suspend fun getMessages(maxMessages: Int = 100): List<ChatMessage> {
        return db.chat.limit(maxMessages).get().await().parseList()
    }

    suspend fun addMessage(chatMessage: ChatMessage) {
        db.chat.add(chatMessage.storable()).await()
    }
}
