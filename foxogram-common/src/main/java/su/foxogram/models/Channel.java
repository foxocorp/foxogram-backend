package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ChannelsConstants;

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

	@OneToOne(mappedBy = "id", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
	public Attachment icon;

	@Column()
	public int type;

	@Column()
	public long flags;

	@ManyToOne()
	@JoinColumn(name = "user_id", nullable = false)
	public User owner;

	@OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Member> members;

	@OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Message> messages;

	@Column()
	public long createdAt;

	public Channel() {
	}

	public Channel(long id, String displayName, String name, long flags, int type, User owner) {
		this.id = id;
		this.displayName = displayName;
		this.name = name.toLowerCase();
		this.type = type;
		this.owner = owner;
		this.flags = flags;
		this.createdAt = System.currentTimeMillis();
	}

	public void addFlag(ChannelsConstants.Flags flag) {
		this.flags |= flag.getBit();
	}

	public void removeFlag(ChannelsConstants.Flags flag) {
		this.flags &= ~flag.getBit();
	}

	public boolean hasFlag(ChannelsConstants.Flags flag) {
		return (this.flags & flag.getBit()) != 0;
	}
}
