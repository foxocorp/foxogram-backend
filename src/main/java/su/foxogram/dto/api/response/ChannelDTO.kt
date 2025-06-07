package su.foxogram.dto.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.Setter
import su.foxogram.model.Channel
import su.foxogram.model.Message

@Getter
@Setter
@Schema(name = "Channel")
data class ChannelDTO(val channel: Channel, val message: Message?) {
    val id: Long = channel.getId()

    val displayName: String? = channel.displayName

    val name: String? = channel.name

    var icon: AttachmentDTO? = if (channel.icon != null) AttachmentDTO(channel.icon) else null

    val type: Int = channel.type

    val flags: Long = channel.flags

    var memberCount = 0

    val owner: UserDTO? = UserDTO(
        channel.owner, null, null,
        includeEmail = false,
        includeChannels = false,
        includeContacts = false
    )

    val createdAt: Long = channel.createdAt

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var lastMessage: MessageDTO? = if (message != null) MessageDTO(message, false) else null
}
