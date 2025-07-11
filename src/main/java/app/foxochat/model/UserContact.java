package app.foxochat.model;

import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table(name = "user_contacts")
public class UserContact {

    @Id
    private long id;

    @Column
    @ManyToOne(foreignKey = "user")
    private User user;

    @Column
    @ManyToOne(foreignKey = "contact")
    private User contact;

    public UserContact() {
    }

    public UserContact(User user, User contact) {
        this.user = user;
        this.contact = contact;
    }
}
