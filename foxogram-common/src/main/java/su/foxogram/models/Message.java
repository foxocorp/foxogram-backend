package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.exceptions.member.MissingPermissionsException;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "messages", indexes = {
		@Index(name = "idx_message_id", columnList = "id"),
		@Index(name = "idx_message_channel", columnList = "channel"),
		@Index(name = "idx_message_id_channel_id", columnList = "id, channel")
})
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

	@Column()
	public String content;

	@ManyToOne
	@JoinColumn(name = "author", nullable = false)
	public Member author;

	@Column()
	public long timestamp;

	@ElementCollection(targetClass = String.class)
	@CollectionTable(name = "attachments", joinColumns = @JoinColumn(name = "message_id"))
	@Column()
	public List<String> attachments;

	@ManyToOne
	@JoinColumn(name = "channel", nullable = false)
	private Channel channel;

	public Message() {
	}

	public Message(long id, Channel channel, String content, long authorId, long timestamp, List<String> attachments) {
		this.id = id;
		this.channel = channel;
		this.author = new Member(authorId);
		this.content = content;
		this.timestamp = timestamp;
		this.attachments = attachments;
	}

	public void isAuthor(Member member) throws MissingPermissionsException {
		if (!author.getUser().getEmail().equals(member.getUser().getUsername()))
			throw new MissingPermissionsException();
	}
}
