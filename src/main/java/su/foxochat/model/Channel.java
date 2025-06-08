package su.foxochat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxochat.constant.ChannelConstant;

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

	@Column
	public String displayName;

	@Column
	public String name;

	@JoinColumn(name = "icon_id")
	@ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	public Attachment icon;

	@Column
	public int type;

	@Column
	public long flags;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	public User owner;

	@OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Member> members;

	@OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Message> messages;

	@Column
	public long createdAt;

	public Channel() {}

	public Channel(String displayName, String name, long flags, int type, User owner) {
		this.displayName = displayName;
		this.name = name.toLowerCase();
		this.type = type;
		this.owner = owner;
		this.flags = flags;
		this.createdAt = System.currentTimeMillis();
	}

	public void addFlag(ChannelConstant.Flags flag) {
		this.flags |= flag.getBit();
	}

	public void removeFlag(ChannelConstant.Flags flag) {
		this.flags &= ~flag.getBit();
	}

	public boolean hasFlag(ChannelConstant.Flags flag) {
		return (this.flags & flag.getBit()) != 0;
	}
}
