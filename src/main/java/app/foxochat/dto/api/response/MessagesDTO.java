package app.foxochat.dto.api.response;

import app.foxochat.model.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Schema(name = "Messages")
public class MessagesDTO {

    private List<MessageDTO> messages;

    public MessagesDTO(List<Message> messages) {
        this.messages = messages.stream()
                .map(m -> new MessageDTO(m, true))
                .collect(Collectors.toList());
    }
}
