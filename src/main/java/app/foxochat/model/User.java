package app.foxochat.model;

import app.foxochat.constant.UserConstant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

	@Column
	public String displayName;

	@Column
	public String username;

	@Column
	public String bio;

	@Column
	private String email;

	@OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<UserContact> contacts;

	@Column
	private int status;

	@Column
	private long statusUpdatedAt;

	@Column
	private String password;

	@Column(nullable = false)
	private int tokenVersion;

	@JoinColumn(name = "avatar_id")
	@ManyToOne(cascade = CascadeType.REMOVE)
	public Attachment avatar;

	@JoinColumn(name = "banner_id")
	@ManyToOne(cascade = CascadeType.REMOVE)
	public Attachment banner;

	@Column
	public long flags;

	@Column
	public int type;

	@Column
	private long createdAt;

	@Column
	private long deletedAt;

	public User() {}

	public User(String username, String email, String password, long flags, int type) {
		this.displayName = null;
		this.username = username.toLowerCase();
		this.bio = null;
		this.email = email;
		this.password = password;
		if (this.contacts != null) this.contacts = contacts.stream()
				.map(userContact -> new UserContact(this, userContact.getContact()))
				.collect(Collectors.toList());
		this.status = UserConstant.Status.OFFLINE.getStatus();
		this.statusUpdatedAt = System.currentTimeMillis();
		this.flags = flags;
		this.type = type;
		this.createdAt = System.currentTimeMillis();
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
