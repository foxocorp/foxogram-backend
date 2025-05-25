package su.foxogram.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_contacts", indexes = {
        @Index(name = "idx_user_contact", columnList = "user_id, contact_id", unique = true)
})
public class UserContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private User contact;

    public UserContact() {
    }

    public UserContact(User user, User contact) {
        this.user = user;
        this.contact = contact;
    }
}
