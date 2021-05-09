package argent.api.controllers

import argent.api.authedWsHandler
import argent.data.chat.ActiveUsersData
import argent.data.chat.ChatMessage
import argent.data.chat.ChatStore
import argent.data.chat.MessagesData
import argent.data.users.User
import argent.util.WithLogger
import argent.util.extra
import argent.util.logger
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.launch
import java.util.Collections

class ChatController(chatStore: ChatStore): WithLogger {

    private class Connection(val session: DefaultWebSocketSession, val user: User)
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    private suspend fun updateActiveUsers() {
        logger.info("Updating all users")
        val activeChatters = ActiveUsersData(connections.map { it.user.name })
        connections.forEach {
            it.session.send(activeChatters.toFrame())
        }
    }

    val chatHandler = authedWsHandler { user ->
        logger.info("New chat connection", extra("userId" to user.user))
        val thisConnection = Connection(this, user)
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
            logger.info("Removing connection", extra("userId" to thisConnection.user.user))
            connections -= thisConnection
            updateActiveUsers()
        }
    }
}
