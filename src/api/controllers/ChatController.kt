package argent.api.controllers

import argent.api.authedWsHandler
import argent.data.chat.ActiveUsersData
import argent.data.chat.ChatMessage
import argent.data.chat.ChatStore
import argent.data.chat.MessagesData
import argent.util.extra
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.Collections

class ChatController(chatStore: ChatStore) {

    private class Connection(val session: DefaultWebSocketSession, val name: String)
    private val logger = LoggerFactory.getLogger("chat")
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    private suspend fun updateActiveUsers() {
        val activeChatters = ActiveUsersData(connections.map { it.name })
        connections.forEach {
            it.session.send(activeChatters.toFrame())
        }
    }

    val chatHandler = authedWsHandler { user ->
        val thisConnection = Connection(this, user.name)
        connections += thisConnection
        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                if (receivedText.isBlank()) continue
                if (receivedText == "getInitialData") {
                    updateActiveUsers()
                    send(MessagesData(chatStore.getMessages()).toFrame())
                } else {
                    val chatMessage = ChatMessage.newMessage(user, receivedText)
                    launch { chatStore.addMessage(chatMessage) }
                    connections.forEach {
                        it.session.send(chatMessage.toFrame())
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Chat error", e)
        } finally {
            logger.info("Removing connection", extra("connectionName" to thisConnection.name))
            connections -= thisConnection
            updateActiveUsers()
        }
    }
}
