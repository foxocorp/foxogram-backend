package app.foxochat.model;

import app.foxochat.constant.ChannelConstant;
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

    @Column
    public String displayName;

    @Column
    public String name;

    @JoinColumn(name = "avatar_id")
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Avatar avatar;

    @JoinColumn(name = "banner_id")
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Avatar banner;

    @Column
    public int type;

    @Column
    public long flags;

    @Column
    public long createdAt;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Member> members;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
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
