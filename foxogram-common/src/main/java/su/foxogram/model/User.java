package su.foxogram.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constant.UserConstant;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
@Table(name = "users", indexes = {
		@Index(name = "idx_user_id", columnList = "id"),
		@Index(name = "idx_user_username", columnList = "username", unique = true),
		@Index(name = "idx_user_email", columnList = "email", unique = true)
})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

	@ManyToOne
	@JoinColumn(name = "contact_id")
	public User contact;

	@Column()
	public String displayName;

	@Column()
	public String username;

	@Column()
	private String email;

	@OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<UserContact> contacts;

	@Column()
	private int status;

	@Column()
	private long statusUpdatedAt;

	@Column()
	private String password;

	@JoinColumn(name = "avatar_id")
	@ManyToOne(cascade = CascadeType.REMOVE)
	public Attachment avatar;

	@Column()
	public long flags;

	@Column()
	public int type;

	@Column()
	private long createdAt;

	@Column()
	private long deletion;

	@Column()
	private String key;

	public User() {
	}

	public User(long id, String displayName, String username, String email, String password, List<User> contacts, int status, long statusUpdatedAt, long flags, int type, long deletion, String key) {
		this.id = id;
		this.contact = this;
		this.displayName = displayName;
		this.username = username.toLowerCase();
		this.email = email;
		this.password = password;
		this.contacts = contacts.stream()
				.map(contact -> new UserContact(this, contact))
				.collect(Collectors.toList());
		this.status = status;
		this.statusUpdatedAt = statusUpdatedAt;
		this.flags = flags;
		this.type = type;
		this.createdAt = System.currentTimeMillis();
		this.deletion = deletion;
		this.key = key;
	}

	public void addFlag(UserConstant.Flags flag) {
		this.flags |= flag.getBit();
	}

	public void removeFlag(UserConstant.Flags flag) {
		this.flags &= ~flag.getBit();
	}

	public boolean hasFlag(UserConstant.Flags flag) {
		return (this.flags & flag.getBit()) != 0;
	}
}
