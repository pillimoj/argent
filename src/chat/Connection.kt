package argent.chat

import io.ktor.http.cio.websocket.DefaultWebSocketSession

class Connection(val session: DefaultWebSocketSession, val name: String)
