package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.MemberConstants;
import su.foxogram.exceptions.member.MissingPermissionsException;

@Setter
@Getter
@Entity
@Table(name = "members", indexes = {
		@Index(name = "idx_member_user_channel", columnList = "user_id, channel_id")
})
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

	@Column()
	public long permissions;

	@ManyToOne()
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne()
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	@Column()
	public long joinedAt;

	public Member() {

	}

	public Member(long id) {
		this.id = id;
	}

	public Member(User user, Channel channel, long permissions) {
		this.user = user;
		this.channel = channel;
		this.permissions = permissions;
		this.joinedAt = System.currentTimeMillis();
	}

	public void addPermission(MemberConstants.Permissions permission) {
		this.permissions |= permission.getBit();
	}

	public void addPermissions(MemberConstants.Permissions... permissions) {
		for (MemberConstants.Permissions permission : permissions) {
			this.permissions |= permission.getBit();
		}
	}

	public void setPermissions(MemberConstants.Permissions... permissions) {
		this.permissions = 0;
		for (MemberConstants.Permissions permission : permissions) {
			this.permissions |= permission.getBit();
		}
	}

	public void removePermission(MemberConstants.Permissions permission) {
		this.permissions &= ~permission.getBit();
	}

	public boolean hasPermission(MemberConstants.Permissions permission) {
		return (this.permissions & permission.getBit()) != 0;
	}

	public void hasPermissions(MemberConstants.Permissions... permissions) throws MissingPermissionsException {
		for (MemberConstants.Permissions permission : permissions) {
			if ((this.permissions & permission.getBit()) == 0) {
				return;
			}
		}

		throw new MissingPermissionsException();
	}

	public boolean hasAnyPermission(MemberConstants.Permissions... permissions) {
		for (MemberConstants.Permissions permission : permissions) {
			if ((this.permissions & permission.getBit()) != 0) {
				return true;
			}
		}

		return false;
	}
}
