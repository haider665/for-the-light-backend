package For.the.light.iot.websocket;

import For.the.light.iot.dto.IotLocationUpdatedEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class IotLocationWebSocketBroadcaster {

    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public void addSession(WebSocketSession session) {
        sessions.add(session);
        log.debug("IoT websocket connected: {}", session.getId());
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
        log.debug("IoT websocket disconnected: {}", session.getId());
    }

    public void broadcast(IotLocationUpdatedEventDto event) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize IoT websocket event", ex);
            return;
        }

        TextMessage message = new TextMessage(payload);
        sessions.removeIf(session -> !session.isOpen());

        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(message);
            } catch (IOException ex) {
                log.warn("Failed sending IoT websocket message to session {}", session.getId(), ex);
                try {
                    session.close();
                } catch (IOException ignored) {
                }
                sessions.remove(session);
            }
        }
    }
}
