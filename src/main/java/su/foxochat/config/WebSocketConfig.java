package su.foxochat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import su.foxochat.handler.structure.EventHandler;

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
