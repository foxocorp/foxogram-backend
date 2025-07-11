package app.foxochat.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(name = "otps")
public class OTP {

    @Id
    public long userId;

    @Column
    public String type;

    @Column
    public String value;

    @Column
    public long issuedAt;

    @Column
    public long expiresAt;

    public OTP() {
    }

    public OTP(long userId, String type, String value, long issuedAt, long expiresAt) {
        this.userId = userId;
        this.type = type;
        this.value = value;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }
}
