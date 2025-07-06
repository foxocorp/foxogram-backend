package app.foxochat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "avatars", indexes = {
        @Index(name = "idx_avatar_id", columnList = "id", unique = true),
        @Index(name = "idx_avatar_user_id", columnList = "id, user_id"),
        @Index(name = "idx_avatar_channel_id", columnList = "id, channel_id")
})
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column
    private String uuid;

    @Column
    private String filename;

    @Column
    private String tumbhash;

    public Avatar() {
    }

    public Avatar(User user, Channel channel, String uuid, String filename, boolean includeUser,
                  boolean includeChannel) {
        if (includeUser) this.user = user;
        if (includeChannel) this.channel = channel;
        this.uuid = uuid;
        this.filename = filename;
    }
}
