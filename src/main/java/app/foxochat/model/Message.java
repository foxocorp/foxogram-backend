package app.foxochat.model;

import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import io.github.joselion.springr2dbcrelationships.annotations.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Table(name = "messages")
public class Message {

    @Id
    public long id;

    @Column
    public String content;

    @Column
    @ManyToOne(foreignKey = "author")
    public Member author;

    @Column
    public long timestamp;

    @Column
    @OneToMany(mappedBy = "message")
    public List<MessageAttachment> attachments;

    @Column
    @ManyToOne(foreignKey = "channel")
    private Channel channel;

    public Message() {
    }

    public Message(Channel channel, String content, Member member, List<Attachment> attachments) {
        this.channel = channel;
        this.author = member;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.attachments = attachments.stream()
                .map(attachment -> new MessageAttachment(this, attachment))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isAuthor(Member member) {
        return author.getUser().getUsername().equals(member.getUser().getUsername());
    }
}
