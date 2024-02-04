package org.sn.socialnetwork.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.sn.socialnetwork.security_and_config.FieldEncryptor;
import java.time.LocalDate;
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

    @Column(name = "security_question")
//    @Convert(converter = FieldEncryptor.class)
    private String securityQuestion;

    @Column(name = "security_answer", nullable = false)
//    @Convert(converter = FieldEncryptor.class)
    private String securityAnswer;

    private boolean verified;

}
