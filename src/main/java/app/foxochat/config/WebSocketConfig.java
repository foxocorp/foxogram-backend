package app.foxochat.config;

import app.foxochat.handler.structure.EventHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final EventHandler eventHandler;

    public WebSocketConfig(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(eventHandler, "/")
                .setAllowedOrigins("*");
    }
}
