package org.sn.socialnetwork.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.sn.socialnetwork.security_and_config.FieldEncryptor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false)
    private String gender;

    @Column(name = "security_question1")
    @Convert(converter = FieldEncryptor.class)
    private String securityQuestion1;

    @Column(name = "security_answer1", nullable = false)
    @Convert(converter = FieldEncryptor.class)
    private String securityAnswer1;

    @Column(name = "security_question2")
    @Convert(converter = FieldEncryptor.class)
    private String securityQuestion2;

    @Column(name = "security_answer2", nullable = false)
    @Convert(converter = FieldEncryptor.class)
    private String securityAnswer2;

    @Column
    private LocalDateTime createdAt;

    private boolean verified;  // For email verification while Registration

//  Following 2 are for 2FA
    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @Column(name = "is_two_factor_enabled")
    private boolean isTwoFactorEnabled = true;

}

