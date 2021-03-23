package argent.chat

import argent.data.users.User
import argent.server.InternalServerError
import argent.util.extra
import io.ktor.auth.principal
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.Route
import io.ktor.websocket.webSocket
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.Collections

suspend fun updateActiveUsers(connections: Set<Connection>) {
    val activeChatters = ActiveUsersData(connections.map { it.name })
    connections.forEach {
        it.session.send(activeChatters.toFrame())
    }
}

fun Route.chat(chatStore: ChatStore) {
    val logger = LoggerFactory.getLogger("chat")
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    webSocket("/chat") {
        val user = call.principal<User>() ?: throw InternalServerError("No principal when connecting to chat")
        val thisConnection = Connection(this, user.name)
        connections += thisConnection
        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                if (receivedText.isBlank()) continue
                if (receivedText == "getInitialData") {
                    updateActiveUsers(connections)
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
            updateActiveUsers(connections)
        }
    }
}
