package ir.entity;

import ir.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken extends BaseEntity {

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    private boolean used;

    public VerificationToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusDays(1); // Token valid for 24 hours
        this.used = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}