package app.foxochat.service;

import java.util.List;

public interface GatewayService {

    void sendMessageToSpecificSessions(List<Long> userIds, int opcode, Object data, String type) throws Exception;
}
