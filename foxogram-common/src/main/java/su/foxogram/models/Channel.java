package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "channels", indexes = {
		@Index(name = "idx_channel_id", columnList = "id", unique = true),
		@Index(name = "idx_channel_name", columnList = "name", unique = true)
})
public class Channel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

	@Column()
	public String displayName;

	@Column()
	public String name;

	@Column()
	public String icon;

	@Column()
	public int type;

	@Column()
	public String owner;

	@OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Member> members;

	@OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Message> messages;

	@Column()
	public long createdAt;

	public Channel() {
	}

	public Channel(long id, String displayName, String name, int type, String owner) {
		this.id = id;
		this.displayName = displayName;
		this.name = name;
		this.type = type;
		this.owner = owner;
		this.createdAt = System.currentTimeMillis();
	}
}
