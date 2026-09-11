package For.the.light.iot.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class IotWebSocketConfig implements WebSocketConfigurer {

    private final IotLocationWebSocketHandler iotLocationWebSocketHandler;

    @Value("${app.custom.frontend-uri}")
    private String frontendUri;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(iotLocationWebSocketHandler, "/ws/iot/locations")
                .setAllowedOrigins(frontendUri);
    }
}
