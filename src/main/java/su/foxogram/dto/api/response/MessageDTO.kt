package su.foxogram.dto.api.response

import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.Setter
import su.foxogram.model.Message
import su.foxogram.model.MessageAttachment
import java.util.stream.Collectors

@Getter
@Setter
@Schema(name = "Message")
data class MessageDTO(val message: Message, val includeChannel: Boolean) {
    val id = message.id

    val content = message.content

    val author = MemberDTO(message.author, false)

    var channel = if (includeChannel) ChannelDTO(message.channel, null) else null

    var attachments: MutableList<AttachmentDTO?>? = if (message.attachments != null) {
        message.attachments.stream()
            .map { messageAttachment: MessageAttachment? -> AttachmentDTO(messageAttachment!!.attachment) }
            .collect(Collectors.toList())
    } else null

    val createdAt: Long = message.timestamp
}
