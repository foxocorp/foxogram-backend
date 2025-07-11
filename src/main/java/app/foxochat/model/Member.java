package app.foxochat.model;

import app.foxochat.constant.MemberConstant;
import app.foxochat.exception.member.MissingPermissionsException;
import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Arrays;

@Setter
@Getter
@Table(name = "members")
public class Member {

    @Id
    public long id;

    @Column
    public long permissions;

    @Column
    public long joinedAt;

    @Column
    @ManyToOne(foreignKey = "user_id")
    private User user;

    @Column
    @ManyToOne(foreignKey = "channel_id")
    private Channel channel;

    public Member() {
    }

    public Member(User user, Channel channel, long permissions) {
        this.user = user;
        this.channel = channel;
        this.permissions = permissions;
        this.joinedAt = System.currentTimeMillis();
    }

    public void addPermission(MemberConstant.Permissions permission) {
        this.permissions |= permission.getBit();
    }

    public void addPermissions(MemberConstant.Permissions... permissions) {
        for (MemberConstant.Permissions permission : permissions) {
            this.permissions |= permission.getBit();
        }
    }

    public void setPermissions(MemberConstant.Permissions... permissions) {
        this.permissions = 0;
        for (MemberConstant.Permissions permission : permissions) {
            this.permissions |= permission.getBit();
        }
    }

    public void removePermission(MemberConstant.Permissions permission) {
        this.permissions &= ~permission.getBit();
    }

    public boolean hasPermission(MemberConstant.Permissions permission) {
        return (this.permissions & permission.getBit()) != 0;
    }

    public void hasPermissions(MemberConstant.Permissions... permissions) throws MissingPermissionsException {
        for (MemberConstant.Permissions permission : permissions) {
            if ((this.permissions & permission.getBit()) == 0) {
                return;
            }
        }

        throw new MissingPermissionsException();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasAnyPermission(MemberConstant.Permissions... permissions) {
        return Arrays.stream(permissions).map(p -> (this.permissions & p.getBit()) != 0).findFirst().get();
    }
}
