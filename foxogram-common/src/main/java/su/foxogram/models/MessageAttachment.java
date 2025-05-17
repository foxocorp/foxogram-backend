package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "message_attachments", indexes = {
		@Index(name = "idx_message_attachment", columnList = "message_id, attachment_id")
})
public class MessageAttachment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", nullable = false)
	private Message message;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attachment_id", nullable = false)
	private Attachment attachment;

	public MessageAttachment() {}

	public MessageAttachment(Message message, Attachment attachment) {
		this.message = message;
		this.attachment = attachment;
	}
}
