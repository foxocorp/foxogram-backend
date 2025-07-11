package app.foxochat.model;

import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(name = "attachments")
public class Attachment {

    @Id
    private long id;

    @ManyToOne(foreignKey = "user_id")
    private User user;

    @Column
    private String uuid;

    @Column
    private String filename;

    @Column
    private String contentType;

    @Column
    private long flags;

    @Column
    private String tumbhash;

    public Attachment() {
    }

    public Attachment(User user, String uuid, String filename, String contentType, long flags) {
        this.user = user;
        this.uuid = uuid;
        this.filename = filename;
        this.contentType = contentType;
        this.flags = flags;
    }
}
