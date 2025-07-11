package app.foxochat.model;

import app.foxochat.constant.ChannelConstant;
import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import io.github.joselion.springr2dbcrelationships.annotations.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter
@Setter
@Table(name = "channels")
public class Channel {

    @Id
    public long id;

    @Column
    public String displayName;

    @Column
    public String name;

    @Column
    @ManyToOne(foreignKey = "avatar")
    public Avatar avatar;

    @Column
    @ManyToOne(foreignKey = "banner")
    public Avatar banner;

    @Column
    public int type;

    @Column
    public long flags;

    @Column
    public long createdAt;

    @OneToMany(mappedBy = "channel")
    private List<Member> members;

    @OneToMany(mappedBy = "channel")
    private List<Message> messages;

    public Channel() {
    }

    public Channel(String displayName, String name, long flags, int type) {
        this.displayName = displayName;
        this.name = name.toLowerCase();
        this.type = type;
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
