package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "attachments", joinColumns = @JoinColumn(name = "message_id"))
	@Column()
	public List<String> attachments;

	@ManyToOne
	@JoinColumn(name = "channel", nullable = false)
	private Channel channel;

	public Message() {
	}

	public Message(Channel channel, String content, Member member, List<String> attachments) {
		this.channel = channel;
		this.author = member;
		this.content = content;
		this.timestamp = System.currentTimeMillis();
		this.attachments = attachments;
	}

	public boolean isAuthor(Member member) {
		return author.getUser().getUsername().equals(member.getUser().getUsername());
	}
}
