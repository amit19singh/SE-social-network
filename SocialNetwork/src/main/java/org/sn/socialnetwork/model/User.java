package org.sn.socialnetwork.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.sn.socialnetwork.security_and_config.FieldEncryptor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", updatable = false, nullable = false)
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

    @Column
    private boolean verified;

    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @Column(name = "is_two_factor_enabled")
    private boolean isTwoFactorEnabled = false;

    @Column(length = 3000)
    private String profilePicUrl;

    @Column
    private String livesIn;

    @Column
    private String userHometown;

    @Column
    private String relationshipStatus;

    @Column
    private boolean isProfilePublic = true;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<UserPost> userPosts = new HashSet<>();
//
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private VerificationToken verificationToken;

}


