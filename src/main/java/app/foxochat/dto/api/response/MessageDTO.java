package app.foxochat.dto.api.response;

import app.foxochat.model.Message;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Schema(name = "Message")
public class MessageDTO {

    private long id;

    private String content;

    private long authorId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long channelId;

    private List<AttachmentDTO> attachments;

    private long createdAt;

    public MessageDTO(Message message, boolean includeChannel) {
        this.id = message.getId();
        this.content = message.getContent();
        this.authorId = message.getAuthor().getId();
        if (includeChannel) this.channelId = message.getChannel().getId();
        if (message.getAttachments() != null) this.attachments = message.getAttachments().stream()
                .map(messageAttachment -> new AttachmentDTO(messageAttachment.getAttachment()))
                .collect(Collectors.toList());
        else this.attachments = new ArrayList<>();
        this.createdAt = message.getTimestamp();
    }
}
