package app.foxochat.handler.structure;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventHandlerRegistry {

    private final Map<Integer, BaseHandler> handlers = new HashMap<>();

    public EventHandlerRegistry(List<BaseHandler> handlers) {
        for (BaseHandler handler : handlers) {
            this.handlers.put(handler.getOpcode(), handler);
        }
    }

    public BaseHandler getHandler(int event) {
        return handlers.get(event);
    }
}
