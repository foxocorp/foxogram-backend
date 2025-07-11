package app.foxochat.model;

import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(name = "avatars")
public class Avatar {

    @Id
    private long id;

    @ManyToOne(foreignKey = "user_id")
    private User user;

    @ManyToOne(foreignKey = "channel_id")
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
