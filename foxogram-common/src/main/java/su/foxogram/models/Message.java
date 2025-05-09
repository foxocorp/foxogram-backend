package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "messages", indexes = {
		@Index(name = "idx_message_id_channel_id", columnList = "id, channel")
})
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

	@Column(columnDefinition = "TEXT")
	public String content;

	@ManyToOne
	@JoinColumn(name = "author", nullable = false)
	public Member author;

	@Column()
	public long timestamp;

	@OneToMany(mappedBy = "id", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<Attachment> attachments;

	@ManyToOne
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	public Message() {
	}

	public Message(Channel channel, String content, Member member, List<Attachment> attachments) {
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
