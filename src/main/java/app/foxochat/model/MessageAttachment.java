package app.foxochat.model;

import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import io.github.joselion.springr2dbcrelationships.annotations.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table(name = "message_attachments")
public class MessageAttachment {

    @Id
    private long id;

    @Column
    @ManyToOne(foreignKey = "message_id")
    private Message message;

    @Column
    @OneToOne(mappedBy = "attachment_id")
    private Attachment attachment;

    public MessageAttachment() {
    }

    public MessageAttachment(Message message, Attachment attachment) {
        this.message = message;
        this.attachment = attachment;
    }
}
