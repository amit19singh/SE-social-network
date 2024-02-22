package org.sn.socialnetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    public enum TokenType {
        REGISTRATION_VERIFICATION,
        PASSWORD_RESET,
        TEMPORARY_VERIFICATION
    }
    @Enumerated(EnumType.STRING)
    private TokenType type;

    public VerificationToken(User user, String token, TokenType type) {
        super();
        this.user = user;
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusMinutes(1440); // Token expires in 24 hours
        this.type = type;
    }

}
