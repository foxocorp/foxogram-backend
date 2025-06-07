package su.foxogram.dto.api.response

import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.Setter
import su.foxogram.model.Message

@Getter
@Setter
@Schema(name = "Messages")
data class MessagesDTO(val messages: MutableList<Message>) {
    var listMessages: MutableList<MessageDTO?>? = null

    init {
        for (message in messages) {
            this.listMessages!!.add(MessageDTO(message, true))
        }
    }
}
